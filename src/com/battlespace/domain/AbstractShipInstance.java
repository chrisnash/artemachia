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

    @Override
    public boolean isAlive()
    {
        return unitsRemaining > 0;
    }

    @Override
    public void updateDamage(DamageEntry damageEntry) throws Exception
    {
        // TODO Auto-generated method stub
        unitsRemaining = damageEntry.remainingShips;
        double m1 = damage.value(false) + damageEntry.damage.value(false);
        double m2 = damage.value(true) + damageEntry.damage.value(true);
        damage = StatFactory.create(m1, m2);
    }
    
    public int getUnits()
    {
        return unitsRemaining;
    }

    @Override
    public Stat getDamage()
    {
        return damage;
    }
    
    public String toString()
    {
        return damage + "(" + unitsRemaining + ")" + template;
    }
}
