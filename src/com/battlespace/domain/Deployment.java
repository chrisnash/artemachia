package com.battlespace.domain;

import java.util.HashMap;
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

}
