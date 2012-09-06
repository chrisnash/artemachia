package com.battlespace.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;

public class TwoTierAttackPlan extends AbstractAttackPlan
{
    Map<Coordinate, TwoTierAttackElement> plan;
    
    public TwoTierAttackPlan(Map<Coordinate, TwoTierAttackElement> plan)
    {
        this.plan = plan;
    }

    @Override
    public Map<Coordinate, List<Coordinate>> getAllTargeting()
    {
       Map<Coordinate, List<Coordinate>> targeting = new HashMap<Coordinate, List<Coordinate>>();
       for(Map.Entry<Coordinate, TwoTierAttackElement> e : plan.entrySet())
       {
           Coordinate k = e.getKey();
           TwoTierAttackElement v = e.getValue();
           targeting.put(k, v.getAllTargets());
       }
       return targeting;
    }
}
