package com.battlespace.domain;

import java.util.Map;

public class ShipEnhancement extends AbstractShip
{

    public ShipEnhancement(String name, Map<String, Stat> torp,
            Map<String, Stat> plas, Stat torpShield, Stat plasShield, Stat dur,
            Stat dom, Stat speed, int units) throws Exception
    {
        super(name, torp, plas, torpShield, plasShield, dur, dom, speed, units);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getReplacementTime()
    {
        return 0;
    }

    @Override
    public double dataValue()
    {
        return 0;
    }

    @Override
    public void updateDataValue(double d)
    {
    }

    @Override
    public void clearDataValue()
    {
    }

}
