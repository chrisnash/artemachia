package com.battlespace.domain.player;

import java.util.List;

import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.Booster;
import com.battlespace.domain.PlayerPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class BoostAllShields extends AbstractPlayerPower
{
    double percent;
    
    public BoostAllShields(List<String> params)
    {
        this.percent=Double.valueOf(params.get(0));
    }

    @Override
    public void upgrade(Booster b) throws Exception
    {
        b.add(new double[]{0,0,0,0,0,0,percent,percent,0,0,0});
    }
}
