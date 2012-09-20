package com.battlespace.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.service.Roller;

public class TwoTierAttackPlan extends AbstractAttackPlan implements BasicAttackPlan
{
    Map<Coordinate, TwoTierAttackElement> plan;
    
    public TwoTierAttackPlan(Map<Coordinate, TwoTierAttackElement> plan, TwoTierAttackStrategy strategy)
    {
        super(strategy);
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
                if(rng.percentChance( ((TwoTierAttackStrategy)strategy).secondPercent)) selector = x.secondTier;
            }
            int selection = rng.select(selector.size());
            out.put(k, selector.get(selection));
        }
        return out;
    }

    @Override
    public double getTorpedoAttackForSize(Coordinate attacker, String size)
    {
        return getStatForSize(attacker, size, 0);
    }

    @Override
    public double getPlasmaAttackForSize(Coordinate attacker, String size)
    {
        return getStatForSize(attacker, size, 3);
    }

    private double getStatForSize(Coordinate attacker, String size, int i)
    {
        int o = "SML".indexOf(size);
        TwoTierAttackElement e = plan.get(attacker);
        return e.getStat(o+i);
    }
}
