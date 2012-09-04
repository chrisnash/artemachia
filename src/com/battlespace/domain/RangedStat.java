package com.battlespace.domain;

import java.util.LinkedList;
import java.util.List;

import com.battlespace.service.StatFactory;

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

    public static List<RangedStat> statsFromDisplay(List<String> attackStats)
    {
        List<RangedStat> results = new LinkedList<RangedStat>();
        for(String stat : attackStats)
        {
            results.add(statFromDisplay(stat));
        }
        return results;
    }

    static RangedStat statFromDisplay(String stat)
    {
        double base = Double.valueOf(stat);
        double min = base - 1;
        if(min<0) min=0;
        double max = base + 1;
        return new RangedStat(min, max);
    }

    public static List<Stat> createEmptyStats()
    {
        List<Stat> l = new LinkedList<Stat>();
        for(int i=0;i<6;i++)
        {
            l.add(new AbsoluteStat(0.0));
        }
        return l;
    }

    public static List<Stat> sum(List<Stat> s1, List<Stat> s2) throws Exception
    {
        List<Stat> out = new LinkedList<Stat>();
        for(int i=0;i<6;i++)
        {
            double v1 = s1.get(i).value(false) + s2.get(i).value(false);
            double v2 = s1.get(i).value(true) + s2.get(i).value(true);
            out.add(StatFactory.create(v1,v2));
        }
        return out;
    }

    public static List<Stat> merge(List<? extends Stat> s1,
            List<? extends Stat> s2) throws Exception
    {
        List<Stat> out = new LinkedList<Stat>();
        for(int i=0; i<6; i++)
        {
            double a1 = s1.get(i).value(false);
            double a2 = s2.get(i).value(false);
            double b1 = s1.get(i).value(true);
            double b2 = s2.get(i).value(true);
            out.add(StatFactory.create((a1>a2)?a1:a2, (b1<b2)?b1:b2));
        }
        return out;
    }

    public static List<Stat> scale(List<? extends Stat> base, double ratio) throws Exception
    {
        List<Stat> out = new LinkedList<Stat>();
        for(Stat s : base)
        {
            out.add(StatFactory.create(ratio*s.value(false), ratio*s.value(true)));
        }
        return out;
    }
    
    public String toString()
    {
        return min+"-"+max;
    }

    public static List<Stat> diff(List<RangedStat> s1,
            List<Stat> s2) throws Exception
    {
        List<Stat> out = new LinkedList<Stat>();
        for(int i=0;i<6;i++)
        {
            double a1 = s1.get(i).value(false);
            double a2 = s2.get(i).value(false);
            double b1 = s1.get(i).value(true);
            double b2 = s2.get(i).value(true);
            
            double m = a1 - b2;
            double n = b1 - a2;
            if(m<0.0) m=0.0;
            out.add(StatFactory.create(m,n));
        }
        return out;
    }

    public void setMin(double value)
    {
        min = value;
    }
    public void setMax(double value)
    {
        max = value;
    }
}
