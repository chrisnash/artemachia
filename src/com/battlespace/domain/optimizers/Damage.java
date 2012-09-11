package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;

public class Damage implements FitnessFunction
{
    public Damage(List<String> params)
    {
        
    }

    @Override
    public double getFitness(SimulatorCollator v)
    {
        return v.getStat("enemyDamage");
    }

}
