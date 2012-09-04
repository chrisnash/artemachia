package com.battlespace.domain.player;

import java.util.List;

import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.Booster;
import com.battlespace.domain.PlayerPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class BoostShields extends AbstractPlayerPower
{
    String size;
    double percent;
    
    public BoostShields(List<String> params)
    {
        this.size = params.get(0);
        this.percent=Double.valueOf(params.get(1));
    }

    @Override
    public void upgrade(Booster b) throws Exception
    {
        if(b.size.equals(size))
        {
            b.add(new double[]{0,0,0,0,0,0,percent,percent,0,0,0});
        }
    }
}
