package com.battlespace.strategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;

public abstract class AbstractAttackPlan implements AttackPlan
{
    @Override
    public List<Map<Coordinate, Coordinate>> getAllAttackCombos()
    {
        List<Map<Coordinate, Coordinate>> out = new LinkedList<Map<Coordinate, Coordinate>>();
        out.add(new HashMap<Coordinate,Coordinate>());
        
        Map<Coordinate, List<Coordinate>> targets = getAllTargeting();
        for(Map.Entry<Coordinate, List<Coordinate>> e : targets.entrySet())
        {
            Coordinate attacker = e.getKey();
            List<Coordinate> defenders = e.getValue();
            
            List<Map<Coordinate, Coordinate>> rewrite = new LinkedList<Map<Coordinate, Coordinate>>();
            for(Map<Coordinate, Coordinate> combo : out)
            {
                for(Coordinate defender : defenders)
                {
                    Map<Coordinate, Coordinate> dup = new HashMap<Coordinate, Coordinate>();
                    dup.putAll(combo);
                    dup.put(attacker,  defender);
                    rewrite.add(dup);
                }
            }
            out = rewrite;
        }
        return out;
    }

}
