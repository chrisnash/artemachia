package com.battlespace.main;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.battlespace.domain.BasePlayerShipFactory;
import com.battlespace.domain.BoostedPlayerShipFactory;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.FileData;
import com.battlespace.domain.OptimizerRecord;
import com.battlespace.domain.OptimizerSettings;
import com.battlespace.domain.PlanetData;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
import com.battlespace.domain.optimizers.FitnessFunction;
import com.battlespace.main.parsers.ParsedCommander;
import com.battlespace.main.parsers.ParsedEnemy;
import com.battlespace.service.CommanderPowerService;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.ObjectCreator;
import com.battlespace.service.Optimizer;
import com.battlespace.service.PlanetService;
import com.battlespace.service.PlayerShipDatabase;
import com.battlespace.service.PlayerSkillModifier;
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
        FileData config = DataLoaderService.loadFile("conf/settings.txt");

        ParsedCommander pc = new ParsedCommander(config, args[0]);
                
        ParsedEnemy pe = new ParsedEnemy(args[1]);
                
        String fitness=config.get("default.optmode");
        if(args.length==3)
        {
            fitness = args[2];
        }
                
        // init
        AttackStrategy attackStrategy = (AttackStrategy)ObjectCreator.createObjectFromConfig("com.battlespace.strategy", config, "attackStrategy");       
        EnemyShipDatabase esd = EnemyShipDatabase.load();
        
        OptimizerSettings settings = new OptimizerSettings();
        
        List<String> availableShips = new LinkedList<String>();
        FileData playerData = DataLoaderService.loadFile("conf/player_data.txt");
        
        BoostedPlayerShipFactory bpsf = pc.createShipFactory(playerData, availableShips);
        
        settings.availableShips = availableShips;
        settings.population = config.getInt("optimizer.population",0);
        settings.mutations = config.getInt("optimizer.mutations",0);
        settings.crossovers = config.getInt("optimizer.crossovers",0);
        settings.iterations = config.getInt("optimizer.iterations",0);
        settings.fitness = (FitnessFunction)ObjectCreator.createObjectFromConfig("com.battlespace.domain.optimizers", config, "optmode."+fitness);
        settings.crossoverAttempts = config.getInt("optimizer.crossoverAttempts", 0);
        settings.report = config.getInt("optimizer.report", 0);
        settings.crossoverPopulation = config.getInt("optimizer.crossoverPopulation", 0);
        settings.mutationPopulation = config.getInt("optimizer.mutationPopulation", 0);
        settings.simulations = config.getInt("optimizer.simulations", 0);
        
        // simple test of the simulator runner
        SimulatorContext context = new SimulatorContext();
        
        context.rng = new RandomRoller();
        context.attackStrategy = attackStrategy;
        context.playerFactory = bpsf;    // no upgrades, commander power etc
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
        //params.playerShips = Arrays.asList(playerShips.split(","));
        params.enemyFormation = FormationService.get(pe.enemyFormation);
        params.enemyShips = Arrays.asList(pe.enemyShips.split(","));
     
        OptimizerRecord out = Optimizer.optimize(context, params, settings);
        System.out.println(out);
    }
}
