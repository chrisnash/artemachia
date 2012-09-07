package com.battlespace.domain.optimizers;

import com.battlespace.domain.SimulatorCollator;

public interface FitnessFunction
{
    double getFitness(SimulatorCollator v);
}
