package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;

public class Score implements FitnessFunction
{
    public Score(List<String> params)
    {
        
    }

    @Override
    public double getFitness(SimulatorCollator v)
    {
        return v.getStat("score");
    }

}
