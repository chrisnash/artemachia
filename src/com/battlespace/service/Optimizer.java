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
import com.battlespace.domain.optimizers.FitnessFunction;

public class Optimizer
{
    public static OptimizerRecord optimize(SimulatorContext context, SimulatorParameters parameters, OptimizerSettings settings) throws Exception
    {
        Roller rng = context.rng;
        
        Map<String, SimulatorCollator> population = new HashMap<String, SimulatorCollator>();
        
        int genes = parameters.playerFormation.shipCount();
        int genomes = settings.availableShips.size();
        
        // first generate an initial population (randomly)
        for(int i=0;i<settings.population + settings.mutations + settings.crossovers;)
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
        
        List<String> fittest = null;
        
        for(int iteration=0;iteration<settings.iterations;iteration++)
        {
            fittest = fitnessEvaluation(population, settings.simulations, settings.fitness, context, parameters, settings.population);
           
            if((iteration % settings.report) == 0)
            {
                String bestKey = fittest.get(0);
                SimulatorCollator bestValue = population.get(bestKey);
                System.out.println("Iteration " + iteration + ": " + new OptimizerRecord(bestKey, settings.fitness.getFitness(bestValue)));                
            }
            
            Set<String> crossovers = new HashSet<String>();
            int coAttempts = 0;
            while( (crossovers.size() < settings.crossovers) && (coAttempts++ < settings.crossoverAttempts) )
            {
                int g1 = rng.select(settings.crossoverPopulation);
                int g2 = g1;
                while(g2==g1) g2 = rng.select(settings.crossoverPopulation);
                List<String> x1 = split(fittest.get(g1));
                List<String> x2 = split(fittest.get(g2));
                // guide crossovers here to be a 50/50 cross
                List<Integer> diffs = new LinkedList<Integer>();
                List<String> out = new LinkedList<String>();
                for(int i=0;i<genes;i++)
                {
                    if( !x1.get(i).equals(x2.get(i)) )
                    {
                        diffs.add(Integer.valueOf(i));
                    }
                    out.add(x1.get(i));
                }
                int hamming = diffs.size();
                if(hamming<2) continue;
                int x2tochoose=hamming;
                // we need to choose hamming/2, if odd, round up or down
                if((x2tochoose&1)==1)
                {
                    if(rng.percentChance(50.0)) x2tochoose++; else x2tochoose--;
                }
                x2tochoose>>=1;    // choose this many x2's
                
                // out of hamming chances, set x2tochoose
                for(int i : diffs)
                {
                    if(rng.percentChance( (100.0*x2tochoose)/hamming ) )
                    {
                        x2tochoose--;
                        out.set(i, x2.get(i));
                    }
                    hamming--;
                }
                String g = combine(out);
                // removing this, since low ship count may mean low biodiversity
                // and we could be stuck here awhile
                if( (population.get(g)==null) && (!crossovers.contains(g)) )
                {
                    crossovers.add(g);
                }
            }
            
            // use mutation to fill up the crossovers
            while(crossovers.size() < settings.mutations + settings.crossovers)
            {
                String source = fittest.get(rng.select(settings.mutationPopulation));
                List<String> g = split(source);
                // note there is a 1/e chance here the item won't mutate, so
                // you might go round here a few times
                for(int i=0;i<genes;i++)
                {
                    if(rng.percentChance(100.0/genes))
                    {
                        g.set(i, settings.availableShips.get(rng.select(genomes)));
                    }
                }
                String g2 = combine(g);
                if( (population.get(g2)==null) && (!crossovers.contains(g2)) )
                {
                    crossovers.add(g2);
                }
            }

            for(String crossover : crossovers)
            {
                population.put(crossover, new SimulatorCollator());
            }
        }
        
        String bestKey = fittest.get(0);
        SimulatorCollator bestValue = population.get(bestKey);
        return new OptimizerRecord(bestKey, settings.fitness.getFitness(bestValue));
    }

    private static List<String> fitnessEvaluation(
            Map<String, SimulatorCollator> population, int simulations, FitnessFunction fitness,
            SimulatorContext context, SimulatorParameters parameters,
            int newPopulation) throws Exception
    {
        // fitness evaluation
        SortedSet<OptimizerRecord> best = new TreeSet<OptimizerRecord>();
        for(Map.Entry<String, SimulatorCollator> e : population.entrySet())
        {
            String s = e.getKey();
            SimulatorCollator v = e.getValue();
            List<String> ships = split(s);
            parameters.playerShips = ships;
            Simulator.simulateMultiple(context, parameters, v, simulations);
            best.add(new OptimizerRecord(s, fitness.getFitness(v)));
            
        }
        
        // keep the new population. Ditch the rest
        List<String> fittest = new LinkedList<String>();
        for(OptimizerRecord o : best)
        {
            if(fittest.size() < newPopulation)
            {
                fittest.add(o.id);
            }
            else
            {
                population.remove(o.id);    // kill this one
            }
        }
        return fittest;
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
