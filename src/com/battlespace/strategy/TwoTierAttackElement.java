package com.battlespace.strategy;

import java.util.LinkedList;
import java.util.List;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Stat;

public class TwoTierAttackElement
{
    public List<Coordinate> closest;
    public List<Coordinate> secondTier;
    public List<Stat> cachedStats;

    public TwoTierAttackElement(List<Coordinate> c, List<Coordinate> s, List<Stat> cachedStats)
    {
        this.closest = c;
        this.secondTier = (s!=null)?s:new LinkedList<Coordinate>();
        this.cachedStats = cachedStats;
    }

    public List<Coordinate> getAllTargets()
    {
        List<Coordinate> out = new LinkedList<Coordinate>();
        out.addAll(closest);
        out.addAll(secondTier);
        return out;
    }

    public double getStat(int i)
    {
        return cachedStats.get(i).value();
    }
}
