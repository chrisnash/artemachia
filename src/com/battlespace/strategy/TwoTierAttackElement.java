package com.battlespace.strategy;

import java.util.LinkedList;
import java.util.List;

import com.battlespace.domain.Coordinate;

public class TwoTierAttackElement
{
    List<Coordinate> closest;
    List<Coordinate> secondTier;

    public TwoTierAttackElement(List<Coordinate> c, List<Coordinate> s)
    {
        closest = c;
        secondTier = (s!=null)?s:new LinkedList<Coordinate>();
    }

    public List<Coordinate> getAllTargets()
    {
        List<Coordinate> out = new LinkedList<Coordinate>();
        out.addAll(closest);
        out.addAll(secondTier);
        return out;
    }
}
