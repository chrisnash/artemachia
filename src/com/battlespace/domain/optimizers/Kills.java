package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;

public class Kills implements FitnessFunction
{
    public Kills(List<String> params)
    {
        
    }

    @Override
    public double getFitness(SimulatorCollator v)
    {
        return v.getStat("enemyKills");
    }

}
