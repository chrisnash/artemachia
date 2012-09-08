package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;

public class Dissipater extends AbstractCommanderPower
{
    double percent;
    
    public Dissipater(List<String> params)
    {
        this.percent = Double.valueOf(params.get(0));
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
        b.add(new double[]{percent,percent,percent, 0,0,0, 0,0, 0,0, 0});
    }

}
