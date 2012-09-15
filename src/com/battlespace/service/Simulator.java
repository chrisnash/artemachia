package com.battlespace.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.RangedStat;
import com.battlespace.domain.Ship;
import com.battlespace.domain.ShipInstance;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
import com.battlespace.domain.Stat;
import com.battlespace.strategy.AttackPlan;

public class Simulator
{
    public static SimulatorCollator simulateMultiple(SimulatorContext context, SimulatorParameters params, SimulatorCollator collator, int count) throws Exception
    {
        if(collator==null)
        {
            collator = new SimulatorCollator();
        }
        for(int i=0;i<count;i++)
        {
            SimulatorResults r = simulate(context, params);
            collator.addResult(r);
        }
        return collator;
    }
    
    public static SimulatorResults simulate(SimulatorContext context, SimulatorParameters params) throws Exception
    {
        // create player ships
        List<ShipInstance> ps = new LinkedList<ShipInstance>();
        for(String name : params.playerShips)
        {
            ps.add(context.playerFactory.createShip(name));
        }
        Deployment player = params.playerFormation.deploy(ps);
        
        // create enemy ships
        List<ShipInstance> es = new LinkedList<ShipInstance>();
        for(String name : params.enemyShips)
        {
            es.add(context.enemyFactory.createShip(name));
        }
        Deployment enemy = params.enemyFormation.deploy(es);
        
        while(player.isAlive() && (context.interception || enemy.isAlive()) )
        {
            if(!enemy.isAlive())
            {
                enemy.reboot();
            }
            
            AttackPlan pap = context.attackStrategy.getAttackPlan(player, enemy);
            AttackPlan eap = context.attackStrategy.getAttackPlan(enemy,  player);
            
            // figure out who's attacking who
            Map<Coordinate,Coordinate> pa = pap.selectAttack(context.rng);
            Map<Coordinate,Coordinate> ea = eap.selectAttack(context.rng);
            
            // compute damage
            Map<Coordinate, Stat> enemyDamage = computeDamage(pa, player, enemy, context.rng.percentChance(context.playerCritChance) ? context.playerCritDamage : 1.0);
            Map<Coordinate, Stat> playerDamage = computeDamage(ea, enemy, player, context.rng.percentChance(context.enemyCritChance) ? context.enemyCritDamage : 1.0);
            
            applyDamage(enemy, enemyDamage);
            applyDamage(player, playerDamage);
        }
        // note if both die, enemy wins
        return new SimulatorResults(player.isAlive(), player, enemy);
    }

    private static Map<Coordinate, Stat> computeDamage(
            Map<Coordinate, Coordinate> pa, Deployment player,
            Deployment enemy, double critmul) throws Exception
    {
        Map<Coordinate, Stat> out = new HashMap<Coordinate, Stat>();
        
        Map<Coordinate, List<Coordinate>> attackersPerDefender = invertAttackers(pa);
        for(Map.Entry<Coordinate, List<Coordinate>> e : attackersPerDefender.entrySet())
        {
            Coordinate dl = e.getKey();
            List<Coordinate> al = e.getValue();
            
            Stat torpedoDamage = StatFactory.create(0.0, 0.0);
            Stat plasmaDamage = StatFactory.create(0.0, 0.0);
            // get the defending ship
            ShipInstance si = enemy.getLivingShip(dl.r, dl.c);
            Ship ss = si.getParent();
            String size = ss.getSize();
            
            // sum stats over all attackers
            for(Coordinate a : al)
            {
                ShipInstance ai = player.getLivingShip(a.r, a.c);
                Ship as = ai.getParent();
                double c = ai.getEffectiveCount();  // scalar for damage inflicted
                
                Stat td = as.getTorpedoDamage(size, c*critmul);
                Stat pd = as.getPlasmaDamage(size, c*critmul);
                
                torpedoDamage = RangedStat.sum2(torpedoDamage, td);
                plasmaDamage = RangedStat.sum2(plasmaDamage, pd);
            }
            
            // apply shields
            List<Stat> shields = ss.getShieldStats();
            double damage = shieldedDamage(torpedoDamage.value(), shields.get(0).value())
                    +shieldedDamage(plasmaDamage.value(), shields.get(1).value());
            out.put(dl, StatFactory.create(damage, damage));
        }
        return out;
    }

    private static double shieldedDamage(double damage, double shield)
    {
        if(shield<=0) return damage;
        if(shield>=1000) return 0.0;
        return damage * (1000.0-shield)/1000.0;
    }
    
    private static Map<Coordinate, List<Coordinate>> invertAttackers(
            Map<Coordinate, Coordinate> attackCombo)
    {
        Map<Coordinate, List<Coordinate>> attackersPerDefender = new HashMap<Coordinate, List<Coordinate>>();
        for(Map.Entry<Coordinate, Coordinate> e : attackCombo.entrySet())
        {
            Coordinate attacker = e.getKey();
            Coordinate defender = e.getValue();
            List<Coordinate> list = attackersPerDefender.get(defender);
            if(list == null)
            {
                list = new LinkedList<Coordinate>();
                attackersPerDefender.put(defender, list);
            }
            list.add(attacker);
        }
        return attackersPerDefender;
    }
    
    private static void applyDamage(Deployment d,
            Map<Coordinate, Stat> damageMap) throws Exception
    {
        for(Map.Entry<Coordinate, Stat> e : damageMap.entrySet())
        {
            Coordinate k = e.getKey();
            Stat v = e.getValue();
            ShipInstance si = d.getLivingShip(k.r, k.c);
            si.setDamage(si.getDamage().value() + v.value());     // new damage amount
        }
        
    }
}
