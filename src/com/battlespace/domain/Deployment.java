package com.battlespace.domain;

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

}
