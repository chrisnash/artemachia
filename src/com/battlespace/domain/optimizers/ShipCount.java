package com.battlespace.domain.optimizers;

import java.util.List;

public class ShipCount extends SecondaryFitnessFunction
{
    public ShipCount(List<String> params)
    {
        super(Double.valueOf(params.get(0)), "shipCount");
    }
}
