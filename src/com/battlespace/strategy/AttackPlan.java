package com.battlespace.strategy;

import java.util.List;
import java.util.Map;

import com.battlespace.domain.Coordinate;

public interface AttackPlan
{
    List<Map<Coordinate, Coordinate>> getAllAttackCombos();
    
    Map<Coordinate, List<Coordinate>> getAllTargeting();
}
