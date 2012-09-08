package com.battlespace.domain.commander;

import java.util.List;

import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;

public class Missileer extends AbstractCommanderPower
{
    String size;
    double percent;
    
    public Missileer(List<String> params)
    {
        this.size = params.get(0);
        this.percent = Double.valueOf(params.get(1));
    }
    
    @Override
    public void upgrade(Booster b) throws Exception
    {
        if(b.size.equals(size))
        {
            b.add(new double[]{percent,percent,percent, 0,0,0, 0,0, 0,0, 0});
        }
    }

}
