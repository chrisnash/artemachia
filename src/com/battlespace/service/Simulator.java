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
import com.battlespace.domain.SimulatorState;
import com.battlespace.domain.Stat;
import com.battlespace.main.viewer.EmptyViewer;
import com.battlespace.main.viewer.ShellViewer;
import com.battlespace.main.viewer.Viewer;
import com.battlespace.strategy.AttackPlan;

public class Simulator
{
    public static SimulatorState simulateMultiple(SimulatorContext context, SimulatorParameters params, SimulatorState collator, int count) throws Exception
    {
        if(collator==null)
        {
            collator = new SimulatorCollator();
        }
        Viewer viewer = (count > 1) ? (new EmptyViewer()) : (new ShellViewer());
        for(int i=0;i<count;i++)
        {
            SimulatorResults r = simulate(context, params, viewer);
            collator.addResult(r);
        }
        return collator;
    }
    
    public static SimulatorResults simulate(SimulatorContext context, SimulatorParameters params, Viewer viewer) throws Exception
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
            viewer.beginTurn(player, enemy);
            
            player.beginTurn();
            enemy.beginTurn();
            
            AttackPlan pap = context.attackStrategy.getAttackPlan(player, enemy);
            AttackPlan eap = context.attackStrategy.getAttackPlan(enemy,  player);
           
            pap.execute(enemy, context.rng, context.rng.percentChance(context.playerCritChance) ? context.playerCritDamage : 1.0, viewer);
            eap.execute(player, context.rng, context.rng.percentChance(context.enemyCritChance) ? context.enemyCritDamage : 1.0, viewer);
            
            viewer.endTurn(player, enemy);
            player.endTurn();
            enemy.endTurn();
        }
        // note if both die, enemy wins
        viewer.beginTurn(player,  enemy);
        return new SimulatorResults(player.isAlive(), player, enemy);
    }
}
