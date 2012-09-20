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
           
            pap.execute(enemy, context.rng, context.rng.percentChance(context.playerCritChance) ? context.playerCritDamage : 1.0);
            eap.execute(player, context.rng, context.rng.percentChance(context.enemyCritChance) ? context.enemyCritDamage : 1.0);
        }
        // note if both die, enemy wins
        return new SimulatorResults(player.isAlive(), player, enemy);
    }
}
