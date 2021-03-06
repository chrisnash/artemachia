package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorState;

public class Kills implements FitnessFunction
{
    public Kills(List<String> params)
    {
        
    }

    @Override
    public double getFitness(SimulatorState v)
    {
        return v.getStat("enemyKills");
    }

}
