package com.battlespace.service;

public interface Roller
{
    boolean percentChance(double playerCritChance);
    int select(int options);
    double random();
}
