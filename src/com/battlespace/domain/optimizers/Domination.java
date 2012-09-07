package com.battlespace.domain.optimizers;

import java.util.List;

public class Domination extends SecondaryFitnessFunction
{
    public Domination(List<String> params)
    {
        super(Double.valueOf(params.get(0)), "domination");
    }
}
