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
import com.battlespace.domain.SimulatorState;
import com.battlespace.domain.optimizers.FitnessFunction;

public class Optimizer
{
    SimulatorContext context;
    SimulatorParameters parameters;
    OptimizerSettings settings;
    
    Roller rng;
    Map<String, SimulatorCollator> population;
    
    int genes;      // number of elements in string
    int genomes;    // number of potential selections for each

    // perturbation state variables
    List<String> fittest = null;
    String lastBestKey = null;
    int perturbationLocation = 0;
    int samePerturbationCount = 0;

    int globalPopulation;
    
    public Optimizer(SimulatorContext context, SimulatorParameters parameters, OptimizerSettings settings)
    {
        this.context = context;
        this.parameters = parameters;
        this.settings = settings;
        
        this.rng = context.rng;
        this.population = new HashMap<String, SimulatorCollator>();

        this.genes = parameters.playerFormation.shipCount();
        this.genomes = settings.availableShips.size();
        
        // calculate genomes^genes. The globalPopulation is a limiter if we don't have many genomes
        // or we are only optimizing a small number of ships.
    
        globalPopulation = 1;
        for(int i=0; i<genes; i++) globalPopulation*=genomes;
        
        // if the global population is small, there's nothing to lose by testing them all.
        // similarly if the global population is just a little larger than our test set,
        // it may be difficult to explore missing elements using just random mutation
        
        // example, we have a population of 100 and ask for 10 mutations of the first 10 elements.
        // suppse we have 5 genomes and 3 genes = gp of 125. Then its quite possible we will
        // spend a long time looking for 10 unique mutations (quite possible we might not find one at all).
        // likewise of the 45 crossover combinations it's not too likely that two will differ at two
        // or more code points. (3*.8*.8*.2 + .8*.8*.8). Use crossoverAttempts as a limiter for both.
        // also if crossover count is less than crossoverAttempts, you may as well try them mechanically.
    }
    
    public OptimizerRecord optimize() throws Exception
    {        
        // first generate an initial population (randomly)
        // note if genomes^genes is small, then this will fail
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
                
        for(int iteration=0;iteration<settings.iterations;iteration++)
        {
            fittest = fitnessEvaluation(population, settings.simulations, settings.fitness, context, parameters, settings.population);
            String bestKey = fittest.get(0);
           
            Set<String> crossovers = new HashSet<String>();
            
            // to attempt to improve convergence, try a perturbation if the bestKey is
            // the same as the last one
            perturbationLocation++;
            perturbationLocation %= genes;
            
            if(bestKey.equals(lastBestKey))
            {
                if(samePerturbationCount < genes)   // don't strain yourself
                {
                    for(int i=0;i<genomes;i++)
                    {
                        List<String> g = split(bestKey);
                        g.set(perturbationLocation, settings.availableShips.get(i));
                        String g2 = combine(g);
                        if( (population.get(g2)==null) && (!crossovers.contains(g2)) )
                        {
                            crossovers.add(g2);
                        }
                    }
                }
                samePerturbationCount++;
            }
            else
            {
                samePerturbationCount = 0;
                lastBestKey = bestKey;
            }

            if((iteration % settings.report) == 0)
            {
                SimulatorCollator bestValue = population.get(bestKey);
                System.out.println("Iteration " + iteration + ": " + new OptimizerRecord(bestKey, settings.fitness.getFitness(bestValue))
                        + " stability: " + samePerturbationCount);                
            }
            


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
                // note the potential number of crossovers, and balanced crossovers, depends on hamming distance
                // H    X      BX
                // 1    0      0  1
                // 2    2      2  1 (2) 1
                // 3    6      6  1 (3 3) 1
                // 4    14     6  1 (4 [6] 4) 1
                // 5    30     20 1 (5 [10 10] 5) 1
                // 6    62     20 1 (6 15 [20] 15 6) 1
                
                // we should assume hamming distances >=3 are very unlikely (and if they occur we should prioritize them
                // for the sake of diversity). We need a better analysis here. We need to optimize convergence time.
                
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
            int muAttempts = 0;
            while( (crossovers.size() < settings.mutations + settings.crossovers) && (muAttempts++ < settings.crossoverAttempts) )
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

    protected static List<String> fitnessEvaluation(
            Map<String, ? extends SimulatorState> population, int simulations, FitnessFunction fitness,
            SimulatorContext context, SimulatorParameters parameters,
            int newPopulation) throws Exception
    {
        // fitness evaluation
        SortedSet<OptimizerRecord> best = new TreeSet<OptimizerRecord>();
        for(Map.Entry<String, ? extends SimulatorState> e : population.entrySet())
        {
            String s = e.getKey();
            SimulatorState v = e.getValue();
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

    protected static String combine(List<String> out)
    {
        StringBuffer sb = new StringBuffer();
        for(String item : out)
        {
            if(sb.length()!=0) sb.append(",");
            sb.append(item);
        }
        return sb.toString();
    }

    protected static List<String> split(String source)
    {
        return Arrays.asList(source.split(","));
    }
}
