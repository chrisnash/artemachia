package com.battlespace.service;

import java.util.Random;

public class RandomRoller implements Roller
{
    Random rng = new Random();

    @Override
    public boolean percentChance(double chance)
    {
        return (rng.nextDouble()*100.0)<chance;
    }

    @Override
    public int select(int options)
    {
        if(options==1) return 0;
        return rng.nextInt(options);
    }
}
