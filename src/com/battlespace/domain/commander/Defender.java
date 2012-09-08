package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class Defender extends AbstractCommanderPower
{
    String size;
    double percent;
    
    public Defender(List<String> params)
    {
        size = params.get(0);
        this.percent = Double.valueOf(params.get(1));
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
        if(b.size.equals(size))
        {
            b.add(new double[]{0,0,0, 0,0,0, percent,percent, 0,0, 0});
        }
    }

}
