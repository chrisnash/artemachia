package com.battlespace.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.HillOptimizerRecord;
import com.battlespace.domain.HillPath;
import com.battlespace.domain.OptimizerRecord;
import com.battlespace.domain.OptimizerSettings;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;

public class HillOptimizer extends Optimizer
{
    public Map<String, HillOptimizerRecord> hillCache;
    public Map<String, HillOptimizerRecord> pathCache;
    
    public HillOptimizer(SimulatorContext context,
            SimulatorParameters parameters, OptimizerSettings settings)
    {
        super(context, parameters, settings);
    }

    public OptimizerRecord optimize() throws Exception
    {
        HillOptimizerRecord bestValue = null;
        hillCache = new HashMap<String, HillOptimizerRecord>();
        
        for(int iteration=0;iteration<settings.iterations;iteration++)
        {
            // select a new random start point
            StringBuffer sb = new StringBuffer();
            for(int j=0;j<genes;j++)
            {
                int g = rng.select(genomes);
                if(j!=0) sb.append(",");
                sb.append(settings.availableShips.get(g));
            }
            String key = sb.toString();
            
            // determine the priority of paths to take
            List<HillPath> paths = new ArrayList<HillPath>();
            
            for(int j=0;j<genes;j++)
            {
                for(int i=0;i<genomes;i++)
                {
                    String s = settings.availableShips.get(i);
                    int c = 0;
                    for(String hill : hillCache.keySet())
                    {
                        List<String> hillG = split(hill);
                        if(hillG.get(j).equals(s))
                        {
                            c++;
                        }
                    }
                    paths.add(new HillPath(j, s, c + rng.random()));
                }
            }
            Collections.sort(paths, null);
            
            // hill climb from there
            HillOptimizerRecord hill = hillClimb(key, paths);
            
            // add the hill
            key = hill.id;
            if(hillCache.get(key) == null)
            {
                hillCache.put(key, hill);
            }
            
            // re-evaluate the hills and show the best
            fittest = fitnessEvaluation(hillCache, settings.simulations, settings.fitness, context, parameters, settings.population);
            String bestKey = fittest.get(0);
            bestValue = hillCache.get(bestKey);
            System.out.println("Iteration " + iteration + ": " + bestValue + " candidates:" + hillCache.size());                
        }
        return bestValue;
    }

    private HillOptimizerRecord hillClimb(String key, List<HillPath> paths) throws Exception
    {
        // start with a fresh path cache
        pathCache = new HashMap<String, HillOptimizerRecord>();
        // make sure the key is in the cache
        cacheInsert(key, paths);
        boolean running = true;
        while(running)
        {
            // just try 1 here to get started
            List<String> fitPaths = fitnessEvaluation(pathCache, 2, settings.fitness, context, parameters, settings.population);
            String bestKey = fitPaths.get(0);
            HillOptimizerRecord bestOpt = pathCache.get(bestKey);
            
            
            int availablePaths = bestOpt.paths.size();

            //System.out.println(bestOpt + " " + availablePaths);

            if(availablePaths == 0) return bestOpt;   // king of the hill
            
            // otherwise take some of the paths and cacheInsert them
            if(availablePaths > 5) availablePaths = 5;
            for(int i=0; i<availablePaths; i++)
            {
                List<String> keys = split(bestKey);
                HillPath toTake = bestOpt.paths.get(i);
                keys.set(toTake.gene, toTake.genome);
                cacheInsert(combine(keys), paths);
            }
            bestOpt.paths = bestOpt.paths.subList(availablePaths, bestOpt.paths.size());    // pop them off
        }
        return null;
    }

    private void cacheInsert(String key, List<HillPath> paths)
    {
        // make sure key is in the path cache, or the hill cache
        HillOptimizerRecord r = pathCache.get(key);
        if(r!=null) return;
        
        r = hillCache.get(key);
        if(r!=null)
        {
            r.paths = paths;
            pathCache.put(key, r);
            return;
        }
        
        // in nothing yet
        pathCache.put(key,  new HillOptimizerRecord(key, settings.fitness, paths, new SimulatorCollator()));
        
    }
}
