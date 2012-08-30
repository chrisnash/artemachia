package com.battlespace.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.service.StatFactory;

public class AbsoluteStat implements Stat
{
    double value;
    
    public AbsoluteStat(double value)
    {
        this.value = value;
    }
    
    public static Map<String, Stat> enhance(Map<String, Stat> stats,
            double percent)
    {
        Map<String, Stat> results = new HashMap<String, Stat>();
        for(Map.Entry<String, Stat> e : stats.entrySet())
        {
            String k = e.getKey();
            Stat v = e.getValue();
            results.put(k, v.enhance(percent));
        }
        return results;
    }

    public static Map<String, Stat> createWeaponStat(List<String> subList)
    {
        Map<String, Stat> out = new HashMap<String, Stat>();
        out.put("S", StatFactory.create(subList.get(0)));
        out.put("M", StatFactory.create(subList.get(1)));
        out.put("L", StatFactory.create(subList.get(2)));
        return out;
    }
    
    @Override
    public Stat enhance(double percent)
    {
        return new AbsoluteStat(value * (100.0+percent)/100.0);
    }

    @Override
    public double value()
    {
        return value;
    }

    @Override
    public double value(boolean max)
    {
        return value;
    }
    
    public String toString()
    {
        return ""+value;
    }

}
