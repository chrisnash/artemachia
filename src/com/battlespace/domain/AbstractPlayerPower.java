package com.battlespace.domain;

public abstract class AbstractPlayerPower implements PlayerPower, Comparable<AbstractPlayerPower>
{
    public int level;
    public String name;

    @Override
    public int compareTo(AbstractPlayerPower arg0)
    {
        return this.level - arg0.level;
    }
}
