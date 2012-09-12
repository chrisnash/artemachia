package com.battlespace.domain.optimizers;

import java.util.List;

import com.battlespace.domain.SimulatorCollator;

public class DataValue implements FitnessFunction
{
    public DataValue(List<String> params)
    {
    }
    
    @Override
    public double getFitness(SimulatorCollator v)
    {
        return v.getStat("dataValue");
    }

}
