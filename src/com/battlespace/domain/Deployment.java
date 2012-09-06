package com.battlespace.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class Deployment
{
    SortedMap<Coordinate, ShipInstance> deploymentMap;
    
    public Deployment(SortedMap<Coordinate, ShipInstance> deployData)
    {
        this.deploymentMap = deployData;
    }

    public List<Stat> getStats() throws Exception
    {
        List<Stat> stats = RangedStat.createEmptyStats();
        
        for(ShipInstance instance : deploymentMap.values())
        {
            List<Stat> shipStat = instance.getEffectiveStats();
            stats = RangedStat.sum(stats, shipStat);
        }
        return stats;
    }

    public Map<String, Double> getSummary()
    {
        Map<String, Double> results = new HashMap<String, Double>();
        for(ShipInstance instance : deploymentMap.values())
        {
            String name = instance.getParent().getName();
            double c = instance.getEffectiveCount();
            if(c > 0.0)
            {
                Double d = results.get(name);
                if(d!=null)
                {
                    c += d.doubleValue();
                }
                results.put(name, Double.valueOf(c));
            }
        }
        return results;
    }

    public Integer frontLine()
    {
        Integer fl = null;
        // the front row is actually the COLUMN of the frontmost living ship
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(!v.isAlive()) continue;
            if((fl==null) || (k.c < fl))
            {
                fl = k.c;
            }
        }
        return fl;
    }

    public ShipInstance getLivingShip(int r, int c)
    {
        Coordinate k = new Coordinate(r, c);
        ShipInstance ship = deploymentMap.get(k);
        if( (ship != null) && (!ship.isAlive()) )
        {
            ship = null;
        }
        return ship;
    }

    public void updateDamage(int r, int c, DamageEntry damageEntry) throws Exception
    {
        if(damageEntry==null) return;
        Coordinate k = new Coordinate(r, c);
        ShipInstance ship = deploymentMap.get(k);
        if(ship==null) throw new Exception("No ship at " + k + " to apply damage");
        ship.updateDamage(damageEntry);
    }

    public Collection<ShipInstance> getAllShips()
    {
        return deploymentMap.values();
    }

    public Map<Coordinate, AttackOptions> getAttackVectors(
            Deployment opponent)
    {
        Map<Coordinate, AttackOptions> results = new HashMap<Coordinate, AttackOptions>();
        int fl = opponent.frontLine();
        for(Map.Entry<Coordinate, ShipInstance> e : deploymentMap.entrySet())
        {
            Coordinate k = e.getKey();
            ShipInstance v = e.getValue();
            if(!v.isAlive()) continue;
            
            AttackVector f = getAttackVector(k, opponent, fl);
            AttackVector r = getAttackVector(k, opponent, fl+1);
            results.put(k, new AttackOptions(f,r));
        }
        return results;
    }

    private AttackVector getAttackVector(Coordinate k, Deployment opponent,
            int fl)
    {
        // allow dead-ahead to be treated as deviation 1. Have seen shots go into the adjacent row.
        Integer deviation = null;
        List<Coordinate> best = new LinkedList<Coordinate>();
        for(int r=0; r<5; r++)
        {
            ShipInstance target = opponent.getLivingShip(r, fl);
            if(target != null)
            {
                // lock on
                int d = r - k.r;
                if(d<0) d=-d;
                if(d==0) d=1;
                if((deviation == null) || (d < deviation.intValue()) )
                {
                    best.clear();
                    best.add(new Coordinate(r, fl));
                    deviation = Integer.valueOf(d);
                }
                else if(d == deviation.intValue())
                {
                    best.add(new Coordinate(r, fl));
                }
            }
        }
        return new AttackVector(fl, deviation, best);
    }

}
