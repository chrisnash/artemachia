package com.battlespace.domain.optimizers;

import com.battlespace.domain.SimulatorCollator;
import com.battlespace.domain.SimulatorState;

public interface FitnessFunction
{
    double getFitness(SimulatorState v);
}
