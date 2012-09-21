package com.battlespace.strategy;

import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;
import com.battlespace.domain.ShipInstance;
import com.battlespace.main.viewer.Viewer;
import com.battlespace.service.Roller;

public interface AttackPlan
{
    List<Map<Coordinate, Coordinate>> getAllAttackCombos();
    
    Map<Coordinate, List<Coordinate>> getAllTargeting();

    double computeDamage(Coordinate attacker, ShipInstance si, double multiplier);
    
    public void execute(Deployment defenders, Roller rng, double multiplier, Viewer viewer) throws Exception;

}
