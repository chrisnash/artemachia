package com.battlespace.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.service.Roller;

public class TwoTierAttackPlan extends AbstractAttackPlan
{
    Map<Coordinate, TwoTierAttackElement> plan;
    TwoTierAttackStrategy strategy;
    
    public TwoTierAttackPlan(Map<Coordinate, TwoTierAttackElement> plan, TwoTierAttackStrategy strategy)
    {
        this.plan = plan;
        this.strategy = strategy;
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

    @Override
    public Map<Coordinate, Coordinate> selectAttack(Roller rng)
    {
        Map<Coordinate, Coordinate> out = new HashMap<Coordinate, Coordinate>();
        for(Map.Entry<Coordinate, TwoTierAttackElement> e : plan.entrySet())
        {
            Coordinate k = e.getKey();
            TwoTierAttackElement x = e.getValue();
            
            List<Coordinate> selector = x.closest;
            if(!x.secondTier.isEmpty())
            {
                if(rng.percentChance(strategy.secondPercent)) selector = x.secondTier;
            }
            int selection = rng.select(selector.size());
            out.put(k, selector.get(selection));
        }
        return out;
    }
}
