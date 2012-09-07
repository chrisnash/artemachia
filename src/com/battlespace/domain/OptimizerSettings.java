package com.battlespace.domain;

import java.util.List;

import com.battlespace.domain.optimizers.FitnessFunction;

public class OptimizerSettings
{
    public List<String> availableShips;
    public int population;
    public int mutations;
    public int crossovers;
    public int iterations;
    public FitnessFunction fitness;

}
