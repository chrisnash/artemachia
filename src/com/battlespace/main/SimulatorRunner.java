package com.battlespace.main;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.battlespace.domain.BasePlayerShipFactory;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.FileData;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
import com.battlespace.domain.SimulatorState;
import com.battlespace.main.parsers.ParsedCommander;
import com.battlespace.main.parsers.ParsedEnemy;
import com.battlespace.main.viewer.ShellViewer;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.ObjectCreator;
import com.battlespace.service.RandomRoller;
import com.battlespace.service.Simulator;
import com.battlespace.strategy.AttackStrategy;

public class SimulatorRunner
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        FileData config = DataLoaderService.loadFile("conf/settings.txt");

        // work out how to parameterize this. Use the similar method as optimize.
        // first parameter, commander (creates the player ship factory)
        // second parameter, player ships
        // third parameter, enemy (planet, definition, etc)
        
        // refactor those argument extractors out
        ParsedCommander pc = new ParsedCommander(config, args[0]);
        String playerShips = args[1];
        ParsedEnemy pe = new ParsedEnemy(args[2]);
        
        // init
        AttackStrategy attackStrategy = (AttackStrategy)ObjectCreator.createObjectFromConfig("com.battlespace.strategy", config, "attackStrategy");       
        EnemyShipDatabase esd = EnemyShipDatabase.load();
   
        // simple test of the simulator runner
        SimulatorContext context = new SimulatorContext();
        
        // because the ship factory needs it
        List<String> availableShips = new LinkedList<String>();
        FileData playerData = DataLoaderService.loadFile("conf/player_data.txt");
        
        context.rng = new RandomRoller();
        context.attackStrategy = attackStrategy;
        context.playerFactory = pc.createShipFactory(playerData, availableShips);
        context.enemyFactory = esd;
        context.playerCritChance = pc.commanderCritical;
        context.playerCritDamage = Double.valueOf(config.get("critical_multiplier"));
        context.interception = pe.interception;
        if(pc.commanderPower != null)
        {
            context.playerCritChance *= pc.commanderPower.criticalMultiplier();
        }
        context.enemyCritChance = Double.valueOf(config.get("enemy_critical_percent"));
        context.enemyCritDamage = Double.valueOf(config.get("critical_multiplier"));
        
        SimulatorParameters params = new SimulatorParameters();
        
        params.playerFormation = FormationService.get(pc.playerFormation);
        params.playerShips = Arrays.asList(playerShips.split(","));
        params.enemyFormation = FormationService.get(pe.enemyFormation);
        params.enemyShips = Arrays.asList(pe.enemyShips.split(","));
        
        //System.out.println(results);
        
        SimulatorState collator = null;

        System.out.println("SIMULATION RESULTS");
        collator = Simulator.simulateMultiple(context,  params, collator, 1000);
        System.out.println(collator.toString());
        System.out.println();
        
        // now include an actual simulator output (looks like a replay)
        SimulatorResults results = Simulator.simulate(context, params, new ShellViewer());
    }

}
