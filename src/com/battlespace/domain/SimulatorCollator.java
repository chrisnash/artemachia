package com.battlespace.domain;

import java.util.HashMap;
import java.util.Map;

public class SimulatorCollator implements SimulatorState
{
    int simulations = 0;
    public Map<String, Double> stats = new HashMap<String, Double>();
    
    public void addResult(SimulatorResults r) throws Exception
    {
        Map<String, Double> newStats = r.getAllStats();
        for(Map.Entry<String, Double> e : newStats.entrySet())
        {
            String k = e.getKey();
            double v = e.getValue().doubleValue();
            Double old = stats.get(k);
            if(old!=null)
            {
                v+=old.doubleValue();
            }
            stats.put(k,  Double.valueOf(v));
        }
        simulations++;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        // estimate margin of error
        double moe = Math.sqrt(1.0 / simulations) * 100;
        
        sb.append(simulations + " (" + moe + "%)\n");
        
        for(Map.Entry<String,Double> e : stats.entrySet())
        {
            String k = e.getKey();
            double v = e.getValue().doubleValue() / simulations;
            sb.append(k +": " + v + "\n");
        }
        return sb.toString();
    }

    public double getStat(String string)
    {
        Double stat = stats.get(string);
        if(stat==null)
        {
            System.out.println("Warning: " + string + " stat is not available");
        }
        return stat.doubleValue() / simulations;
    }
}
