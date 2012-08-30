package com.battlespace.domain;

public class RangedStat implements Stat
{
    double min;
    double max;
    
    public RangedStat(double min, double max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public Stat enhance(double percent)
    {
        double factor = (100.0+percent)/100.0;
        return new RangedStat(min*factor, max*factor);
    }

    @Override
    public double value()
    {
        return value(true);
    }

    @Override
    public double value(boolean ismax)
    {
        return ismax ? max : min;
    }
}
