package com.battlespace.strategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.Ship;
import com.battlespace.domain.ShipInstance;
import com.battlespace.domain.Stat;
import com.battlespace.main.viewer.Viewer;
import com.battlespace.service.Roller;

public abstract class AbstractAttackPlan implements AttackPlan
{
    public AttackStrategy strategy;
    
    protected AbstractAttackPlan(AttackStrategy strategy)
    {
        this.strategy = strategy;
    }
    
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

    @Override
    public double computeDamage(Coordinate attacker, ShipInstance si,
            double multiplier)
    {
        Ship parent = si.getParent();
        String size = parent.getSize();
        double ta = getTorpedoAttackForSize(attacker, size);
        double pa = getPlasmaAttackForSize(attacker, size);
        List<Stat> shields = parent.getShieldStats();
        double ts = shields.get(0).value();
        double ps = shields.get(1).value();
        
        return (shieldedDamage(ta,ts) + shieldedDamage(pa,ps))*multiplier;
    }
    
    public abstract double getTorpedoAttackForSize(Coordinate attacker, String size);
    public abstract double getPlasmaAttackForSize(Coordinate attacker, String size);    

    private static double shieldedDamage(double damage, double shield)
    {
        if(shield<=0) return damage;
        if(shield>=1000) return 0.0;
        return damage * (1000.0-shield)/1000.0;
    }
    
    @Override
    public void execute(Deployment defenders, Roller rng, double multiplier, Viewer viewer)
            throws Exception
    {
        AttackProcessor processor = strategy.getProcessor();
        processor.process(this, defenders, rng, multiplier, viewer);
    }

}
