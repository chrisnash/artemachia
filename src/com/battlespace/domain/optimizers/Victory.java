package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorState;

public class Victory implements FitnessFunction
{
    public Victory(List<String> param)
    {    
    }
    
    @Override
    public double getFitness(SimulatorState v)
    {
        return v.getStat("victoryPercent");
    }

}
