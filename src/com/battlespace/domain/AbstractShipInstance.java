package com.battlespace.domain;

import java.util.List;

import com.battlespace.service.StatFactory;

public class AbstractShipInstance implements ShipInstance
{
    Ship template;
    Stat damage;
    int unitsRemaining;
    
    public AbstractShipInstance(Ship parent) throws Exception
    {
        this.template = parent;
        this.damage = StatFactory.create(0.0, 0.0);
        this.unitsRemaining = parent.getUnits();
    }
    
    @Override
    public List<Stat> getEffectiveStats() throws Exception
    {
        double ratio = getEffectiveCount();        
        List<Stat> base = template.getSummaryStats();
        return RangedStat.scale(base, ratio);
    }
    
    public double getEffectiveCount()
    {
        double ratio = unitsRemaining;
        ratio /= template.getUnits();
        return ratio;
    }
    
    public Ship getParent()
    {
        return template;
    }
}
