package com.battlespace.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.battlespace.domain.OptimizerRecord;
import com.battlespace.domain.OptimizerSettings;
import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorContext;
import com.battlespace.domain.SimulatorParameters;

public class Optimizer
{
    public static OptimizerRecord optimize(SimulatorContext context, SimulatorParameters parameters, OptimizerSettings settings) throws Exception
    {
        OptimizerRecord output = null;
        Roller rng = context.rng;
        
        Map<String, SimulatorCollator> population = new HashMap<String, SimulatorCollator>();
        
        int genes = parameters.playerFormation.shipCount();
        int genomes = settings.availableShips.size();
        
        // first generate an initial population (randomly)
        for(int i=0;i<settings.population;)
        {
            StringBuffer sb = new StringBuffer();
            for(int j=0;j<genes;j++)
            {
                int g = rng.select(genomes);
                if(j!=0) sb.append(",");
                sb.append(settings.availableShips.get(g));
            }
            String key = sb.toString();
            if(population.get(key)==null)
            {
                population.put(key, new SimulatorCollator());
                i++;
            }
        }
        
        for(int iteration=0;iteration<settings.iterations;iteration++)
        {
            List<String> sources = new LinkedList<String>();
            sources.addAll(population.keySet());
            
            Set<String> mutations = new HashSet<String>();
            while(mutations.size() < settings.mutations)
            {
                String source = sources.get(rng.select(settings.population));
                List<String> g = split(source);
                for(int i=0;i<genes;i++)
                {
                    if(rng.percentChance(100.0/genes))
                    {
                        g.set(i, settings.availableShips.get(rng.select(genomes)));
                    }
                }
                String g2 = combine(g);
                if( (population.get(g2)==null) && (!mutations.contains(g2)) )
                {
                    mutations.add(g2);
                }
            }
            for(String mutation : mutations)
            {
                population.put(mutation, new SimulatorCollator());
            }
            
            Set<String> crossovers = new HashSet<String>();
            while(crossovers.size() < settings.crossovers)
            {
                int g1 = rng.select(settings.population);
                int g2 = g1;
                while(g2==g1) g2 = rng.select(settings.population);
                List<String> x1 = split(sources.get(g1));
                List<String> x2 = split(sources.get(g2));
                List<String> out = new LinkedList<String>();
                for(int i=0;i<genes;i++)
                {
                    out.add(rng.percentChance(50.0) ? x1.get(i) : x2.get(i));
                }
                String g = combine(out);
                // removing this, since low ship count may mean low biodiversity
                // and we could be stuck here awhile
                //if( (population.get(g)==null) && (!crossovers.contains(g)) )
                //{
                    crossovers.add(g);
                //}
            }
            for(String crossover : crossovers)
            {
                population.put(crossover, new SimulatorCollator());
            }
            
            // fitness evaluation
            SortedSet<OptimizerRecord> best = new TreeSet<OptimizerRecord>();
            for(Map.Entry<String, SimulatorCollator> e : population.entrySet())
            {
                String s = e.getKey();
                SimulatorCollator v = e.getValue();
                List<String> ships = split(s);
                parameters.playerShips = ships;
                Simulator.simulateMultiple(context, parameters, v, 100);
                best.add(new OptimizerRecord(s, settings.fitness.getFitness(v)));
                
            }
            //System.out.println(population.size());
            //System.out.println(best.size());
            
            Iterator<OptimizerRecord> it = best.iterator();
            Map<String, SimulatorCollator> rewrite = new HashMap<String, SimulatorCollator>();
            for(int i=0; i<settings.population; i++)
            {
                OptimizerRecord o = it.next();
                if(i==0)
                {
                    output = o;
                    //System.out.println("Iteration " + iteration + " best so far " + o.id + " with fitness " + o.fitness);
                }
                rewrite.put(o.id, population.get(o.id));
            }
            population = rewrite;
        }
        return output;
    }

    private static String combine(List<String> out)
    {
        StringBuffer sb = new StringBuffer();
        for(String item : out)
        {
            if(sb.length()!=0) sb.append(",");
            sb.append(item);
        }
        return sb.toString();
    }

    private static List<String> split(String source)
    {
        return Arrays.asList(source.split(","));
    }
}
