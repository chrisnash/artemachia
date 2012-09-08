package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class Striker implements CommanderPower
{
    double factor;
    
    public Striker(List<String> params)
    {
        this.factor = Double.valueOf(params.get(0));
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
        // TODO add a new booster column for critical rate
    }

    @Override
    public double criticalMultiplier()
    {
        return factor;
    }

}
