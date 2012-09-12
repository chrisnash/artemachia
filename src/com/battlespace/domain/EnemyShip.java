package com.battlespace.domain;

import java.util.Map;

public class EnemyShip extends AbstractShip
{
    double dataValue = 0.0;

    public EnemyShip(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
        // TODO Auto-generated constructor stub
    }

    public EnemyShipInstance createInstance() throws Exception
    {
        return new EnemyShipInstance(this);
    }

    @Override
    public int getReplacementTime()
    {
        return 0;
    }

    @Override
    public double dataValue()
    {
        return dataValue;
    }

    @Override
    public void updateDataValue(double d)
    {
        // if d is between durability min and max, then this point has value
        double dmin = durability.value(false);
        double dmax = durability.value(true);
        if((d>dmin)&&(d<dmax))
        {
            double d1 = d-dmin;
            double d2 = dmax-d;
            double d3 = (d1<d2)?d1:d2;
            if(d3>dataValue)
            {
                dataValue = d3;
            }
        }
    }
}
