package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorState;

public class Damage implements FitnessFunction
{
    public Damage(List<String> params)
    {
        
    }

    @Override
    public double getFitness(SimulatorState v)
    {
        return v.getStat("enemyDamage");
    }

}
