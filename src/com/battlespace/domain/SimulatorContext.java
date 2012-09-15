package com.battlespace.domain;

import com.battlespace.service.Roller;
import com.battlespace.strategy.AttackStrategy;

public class SimulatorContext
{
    public Roller rng;
    
    public AttackStrategy attackStrategy;
    public ShipFactory playerFactory;
    public ShipFactory enemyFactory;
    
    public double playerCritChance;
    public double playerCritDamage;
    
    public double enemyCritChance;
    public double enemyCritDamage;
    
    public boolean interception;
}
