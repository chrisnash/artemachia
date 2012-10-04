package com.battlespace.domain.optimizers;

import java.util.List;

public class Heroism extends SecondaryFitnessFunction
{
    public Heroism(List<String> params)
    {
        super(Double.valueOf(params.get(0)), "heroism");
    }
}
