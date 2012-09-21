package com.battlespace.strategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Stat;
import com.battlespace.service.Roller;

public class HalifixAttackPlan extends AbstractAttackPlan implements RollingAttackPlan
{
    SortedSet<Coordinate> sortedAttackers;
    Map<Coordinate, List<List<Coordinate>>> storage;
    Map<Coordinate, List<Stat>> stats;
    
    public HalifixAttackPlan(SortedSet<Coordinate> sortedAttackers,
            Map<Coordinate, List<List<Coordinate>>> storage, Map<Coordinate, List<Stat>> stats, HalifixAttackStrategy strategy)
    {
        super(strategy);
        this.sortedAttackers = sortedAttackers;
        this.storage = storage;
        this.stats = stats;
    }

    @Override
    public Map<Coordinate, List<Coordinate>> getAllTargeting()
    {
        Map<Coordinate, List<Coordinate>> targeting = new HashMap<Coordinate, List<Coordinate>>();
        
        // this returns all the possible targets for each attacker. Trivialish.
        for(Map.Entry<Coordinate, List<List<Coordinate>>> e : storage.entrySet() )
        {
            Coordinate a = e.getKey();
            List<List<Coordinate>> v = e.getValue();
            List<Coordinate> folded = new LinkedList<Coordinate>();
            for(List<Coordinate> tier : v)
            {
                folded.addAll(tier);
            }
            targeting.put(a, folded);
        }
        return targeting;
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
        return stats.get(attacker).get(o+i).value();
    }

    @Override
    public SortedSet<Coordinate> getAttackerOrder()
    {
        return sortedAttackers;
    }

    @Override
    public Coordinate selectSingleAttack(Coordinate attacker,
            Collection<Coordinate> defenders, Roller rng)
    {
        SortedMap<Integer, List<Coordinate>> tiers = new TreeMap<Integer, List<Coordinate>>();
        
        for(Coordinate defender : defenders)
        {
            int ro = attacker.r - defender.r;
            if(ro<0) ro=-ro;
            ro *= ((HalifixAttackStrategy)strategy).rowWeight;
            ro += defender.c;   // later columns, later tier
            Integer o = Integer.valueOf(ro);
            List<Coordinate> l = tiers.get(o);
            if(l==null)
            {
                l = new LinkedList<Coordinate>();
                tiers.put(o,l);
            }
            l.add(defender);
        }
        // build the attack element. It's the first map element, and the second, if it exists
        // AND is in the next line
        Iterator<Map.Entry<Integer, List<Coordinate>>> it = tiers.entrySet().iterator();
        Map.Entry<Integer, List<Coordinate>> firstTier = it.hasNext() ? it.next() : null;
        Map.Entry<Integer, List<Coordinate>> secondTier = it.hasNext() ? it.next() : null;
        
        // nothing?
        if(firstTier==null) return null;
        // second tier?
        if(secondTier != null)
        {
            if(rng.percentChance( ((HalifixAttackStrategy)strategy).secondPercent))
            {
                firstTier = secondTier;
            }
        }
        List<Coordinate> l = firstTier.getValue();
        return l.get(rng.select(l.size()));
    }
}
