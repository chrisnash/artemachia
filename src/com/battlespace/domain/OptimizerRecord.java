package com.battlespace.domain;

public class OptimizerRecord implements Comparable<OptimizerRecord>
{
    public String id;
    public double fitness;
    
    public OptimizerRecord(String id, double fitness)
    {
        this.id = id;
        this.fitness = fitness;
    }

    @Override
    public int compareTo(OptimizerRecord arg0)
    {
        double d = this.fitness - arg0.fitness;
        if(d==0)
        {
            return this.id.compareTo(arg0.id);  // never identical
        }
        return (int)Math.signum(-d);
    }
    
    public String toString()
    {
        return id + " (" + fitness + ")";
    }
}
