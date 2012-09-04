package com.battlespace.domain;

public class Booster
{
    public static final int BOOST_SIZE = 11;
    public String size;
    public double boosts[] = new double[BOOST_SIZE];
    
    public Booster(String size)
    {
        this.size = size;
    }

    public void skillUpgrade(double commanderBoost)
    {
        for(int i=0;i<6;i++)
        {
            boosts[i] += commanderBoost;
        }
    }

    public void add(double[] ds)
    {
        for(int i=0;i<BOOST_SIZE;i++)
        {
            boosts[i] += ds[i];
        }
    }
}
