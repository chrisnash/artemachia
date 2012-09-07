package com.battlespace.domain.optimizers;

import com.battlespace.domain.SimulatorCollator;

public abstract class SecondaryFitnessFunction implements FitnessFunction
{
    double victoryThreshold;
    String secondaryStat;
    
    public SecondaryFitnessFunction(double t, String s)
    {
        this.victoryThreshold = t;
        this.secondaryStat = s;
    }

    @Override
    public double getFitness(SimulatorCollator v)
    {
        double q = v.getStat("victoryPercent") - victoryThreshold;
        if(q>0)
        {
            q = v.getStat(secondaryStat);
        }
        return q;
    }
}
