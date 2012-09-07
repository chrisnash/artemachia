package com.battlespace.main;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.battlespace.domain.BasePlayerShipFactory;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.FileData;
import com.battlespace.domain.OptimizerRecord;
import com.battlespace.domain.OptimizerSettings;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.ObjectCreator;
import com.battlespace.service.Optimizer;
import com.battlespace.service.RandomRoller;
import com.battlespace.service.Simulator;
import com.battlespace.strategy.AttackStrategy;

public class OptimizerRunner
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        String playerFormation = "Telescopium";
        //String playerShips = "M-1 Destroyer Ship a,L-1 Destroyer Ship a,L-1 Destroyer Ship a,M-1 Destroyer Ship a";
        String enemyFormation = "Corvus";
        String enemyShips = "S Anathema Ship b,M Anathema Ship b,M Anathema Ship b,M Anathema Ship b,S Anathema Ship b";
        
        // init
        FileData config = DataLoaderService.loadFile("conf/settings.txt");
        AttackStrategy attackStrategy = (AttackStrategy)ObjectCreator.createObjectFromConfig("com.battlespace.strategy", config, "attackStrategy");       
        EnemyShipDatabase esd = EnemyShipDatabase.load();
        
        OptimizerSettings settings = new OptimizerSettings();
        
        List<String> availableShips = new LinkedList<String>();
        FileData playerData = DataLoaderService.loadFile("conf/player_data.txt");
        Set<String> keys = playerData.getKeys();
        for(String key : keys)
        {
            // will eventually have to check for valid ship keys
            availableShips.add(key);
        }

        settings.availableShips = availableShips;
        settings.population = config.getInt("optimizer.population",0);
        settings.mutations = config.getInt("optimizer.mutations",0);
        settings.crossovers = config.getInt("optimizer.crossovers",0);
        settings.iterations = config.getInt("optimizer.iterations",0);
        
        // simple test of the simulator runner
        SimulatorContext context = new SimulatorContext();
        
        context.rng = new RandomRoller();
        context.attackStrategy = attackStrategy;
        context.playerFactory = new BasePlayerShipFactory();    // no upgrades, commander power etc
        context.enemyFactory = esd;
        context.playerCritChance = 1.0; // percent
        context.playerCritDamage = 1.30;
        context.enemyCritChance = 1.0;
        context.enemyCritDamage = 1.30;
        
        SimulatorParameters params = new SimulatorParameters();
        
        params.playerFormation = FormationService.get(playerFormation);
        //params.playerShips = Arrays.asList(playerShips.split(","));
        params.enemyFormation = FormationService.get(enemyFormation);
        params.enemyShips = Arrays.asList(enemyShips.split(","));
     
        OptimizerRecord out = Optimizer.optimize(context, params, settings);
        System.out.println(out);
    }
}
