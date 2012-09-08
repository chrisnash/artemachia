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
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
import com.battlespace.domain.optimizers.FitnessFunction;
import com.battlespace.service.CommanderPowerService;
import com.battlespace.service.DataLoaderService;
import com.battlespace.service.FormationService;
import com.battlespace.service.ObjectCreator;
import com.battlespace.service.Optimizer;
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

        // parameters
        // param 1 is player formation,(commander),(luck),(skill)
        //System.out.println(args[0]);
        String[] playerArgs = args[0].split(",");
        String playerFormation = playerArgs[0];

        int commanderSkill = 0;
        if(playerArgs.length > 1)
        {
            commanderSkill = Integer.valueOf(playerArgs[1]);
        }
        double commanderBoost = commanderSkill/Double.valueOf(config.get("skill_factor"));
        
        int commanderLuck = 0;
        if(playerArgs.length > 2)
        {
            commanderLuck = Integer.valueOf(playerArgs[2]);
        }
        double commanderCritical = commanderLuck/Double.valueOf(config.get("luck_factor"));
        
        CommanderPower commanderPower = null;
        if(playerArgs.length>3)
        {
            commanderPower = CommanderPowerService.get(playerArgs[3]);
        }
        
        // param 2 is enemy selection
        // (planet)
        // (planet level),(config)
        // (formation),(ship,ship,ship...)
        String[] enemyArgs = args[1].split(",");
        String enemyFormation = null;
        String enemyShips = null;
        if(enemyArgs.length==1)
        {
            // lookup planet by id
        }
        else if(enemyArgs.length==2)
        {
            // lookup planet by level and config
        }
        else
        {
            // formation, ship, ship, ship....
            enemyFormation = enemyArgs[0];
            StringBuffer sb = new StringBuffer();
            for(int i=1;i<enemyArgs.length;i++)
            {
                if(i!=1) sb.append(",");
                sb.append(enemyArgs[i]);
            }
            enemyShips = sb.toString();
        }
        
        String fitness="victory";
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
        
        BoostedPlayerShipFactory bpsf = new BoostedPlayerShipFactory();
        int militarySkill = playerData.getInt("military.skill", 0);
        
        Set<String> keys = playerData.getKeys();
        for(String key : keys)
        {
            // will eventually have to check for valid ship keys
            if(!key.contains("."))      // all others contain a dot
            {
                // perfom boost calculations on this ship
                PlayerShip psi = PlayerShipDatabase.lookup(key);
                // get the ug as an array of int
                int[] ug = new int[4];
                String ugString = playerData.get(key);
                for(int i=0;i<4;i++)
                {
                    ug[i] = ugString.codePointAt(i) - '0';
                }
                psi = psi.applyUpgrades( ug, false);    // allow sim to run even with missing files
                Booster booster = new Booster(psi.size);
                PlayerSkillModifier.upgrade(booster, militarySkill);
                if(commanderPower!=null)
                {
                    commanderPower.upgrade(booster);
                }
                booster.skillUpgrade(commanderBoost);
                psi = psi.applyBooster(booster);
                
                bpsf.register(key, psi);
                
                // and add it to the list of ships the GA can use
                availableShips.add(key);
            }
        }

        settings.availableShips = availableShips;
        settings.population = config.getInt("optimizer.population",0);
        settings.mutations = config.getInt("optimizer.mutations",0);
        settings.crossovers = config.getInt("optimizer.crossovers",0);
        settings.iterations = config.getInt("optimizer.iterations",0);
        settings.fitness = (FitnessFunction)ObjectCreator.createObjectFromConfig("com.battlespace.domain.optimizers", config, "optmode."+fitness);
        
        // simple test of the simulator runner
        SimulatorContext context = new SimulatorContext();
        
        context.rng = new RandomRoller();
        context.attackStrategy = attackStrategy;
        context.playerFactory = new BasePlayerShipFactory();    // no upgrades, commander power etc
        context.enemyFactory = esd;
        context.playerCritChance = commanderCritical;
        context.playerCritDamage = Double.valueOf(config.get("critical_multiplier"));
        if(commanderPower != null)
        {
            context.playerCritChance *= commanderPower.criticalMultiplier();
        }
        context.enemyCritChance = Double.valueOf(config.get("enemy_critical_percent"));
        context.enemyCritDamage = Double.valueOf(config.get("critical_multiplier"));
        
        SimulatorParameters params = new SimulatorParameters();
        
        params.playerFormation = FormationService.get(playerFormation);
        //params.playerShips = Arrays.asList(playerShips.split(","));
        params.enemyFormation = FormationService.get(enemyFormation);
        params.enemyShips = Arrays.asList(enemyShips.split(","));
     
        OptimizerRecord out = Optimizer.optimize(context, params, settings);
        System.out.println(out);
    }
}
