package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;

public class Victory implements FitnessFunction
{
    public Victory(List<String> param)
    {    
    }
    
    @Override
    public double getFitness(SimulatorCollator v)
    {
        return v.getStat("victoryPercent");
    }

}
