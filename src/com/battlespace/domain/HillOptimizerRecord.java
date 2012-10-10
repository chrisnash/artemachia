package com.battlespace.domain;

import java.util.List;

import com.battlespace.domain.optimizers.FitnessFunction;

public class HillOptimizerRecord extends OptimizerRecord implements SimulatorState
{
    public List<HillPath> paths;
    public SimulatorCollator collator;
    FitnessFunction ff;

    public HillOptimizerRecord(String id, FitnessFunction ff, List<HillPath> paths, SimulatorCollator collator)
    {
        super(id, 0.0);
        this.ff = ff;
        this.paths = paths;
        this.collator = collator;
    }

    @Override
    public void addResult(SimulatorResults r) throws Exception
    {
        collator.addResult(r);
    }

    @Override
    public double getStat(String string)
    {
        return collator.getStat(string);
    }
    
    public double getFitness()
    {
        return ff.getFitness(collator);
    }
    
    public String toString()
    {
        return super.toString() + " (tests: " + collator.simulations + ")";
    }
}
