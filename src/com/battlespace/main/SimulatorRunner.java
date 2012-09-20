package com.battlespace.main;

import java.util.Arrays;

import com.battlespace.domain.BasePlayerShipFactory;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.FileData;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;
import com.battlespace.domain.SimulatorResults;
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
        // work out how to parameterize this. Use the similar method as optimize.
        // first parameter, commander (creates the player ship factory)
        // second parameter, player ships
        // third parameter, enemy (planet, definition, etc)
        
        // refactor those argument extractors out
        
        String playerFormation = "Telescopium";
        String playerShips = "M-1 Destroyer Ship a,L-1 Destroyer Ship a,L-1 Destroyer Ship a,M-1 Destroyer Ship a";
        String enemyFormation = "Corvus";
        String enemyShips = "S Anathema Ship b,M Anathema Ship b,M Anathema Ship b,M Anathema Ship b,S Anathema Ship b";
        
        // init
        FileData config = DataLoaderService.loadFile("conf/settings.txt");
        AttackStrategy attackStrategy = (AttackStrategy)ObjectCreator.createObjectFromConfig("com.battlespace.strategy", config, "attackStrategy");       
        EnemyShipDatabase esd = EnemyShipDatabase.load();
   
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
        params.playerShips = Arrays.asList(playerShips.split(","));
        params.enemyFormation = FormationService.get(enemyFormation);
        params.enemyShips = Arrays.asList(enemyShips.split(","));
        
        SimulatorResults results = Simulator.simulate(context, params);
        //System.out.println(results);
        
        SimulatorCollator collator = null;
        
        for(int i=0;i<10;i++)
        {
            collator = Simulator.simulateMultiple(context,  params, collator, 100);
            System.out.println(collator.toString());
        }
    }

}
