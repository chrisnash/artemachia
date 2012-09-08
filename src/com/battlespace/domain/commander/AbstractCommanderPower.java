package com.battlespace.domain.commander;

import com.battlespace.domain.CommanderPower;

public abstract class AbstractCommanderPower implements CommanderPower
{
    public double criticalMultiplier()
    {
        return 1.0;
    }
}
