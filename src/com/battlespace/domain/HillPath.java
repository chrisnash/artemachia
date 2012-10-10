package com.battlespace.domain;

public class HillPath implements Comparable<HillPath>
{
    public int gene;
    public String genome;
    public double weight;
    
    public HillPath(int gene, String genome, double weight)
    {
        this.gene = gene;
        this.genome = genome;
        this.weight = weight;
    }

    @Override
    public int compareTo(HillPath arg0)
    {
        return (int)Math.signum(this.weight - arg0.weight);
    }
}
