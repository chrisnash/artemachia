package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class Safeguard implements CommanderPower
{
    double percent;
    
    public Safeguard(List<String> params)
    {
        this.percent = Double.valueOf(params.get(0));
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
        b.add(new double[]{0,0,0, 0,0,0, percent,0, 0,0, 0});
    }

}
