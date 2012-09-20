package com.battlespace.strategy;

import java.util.Map;

import com.battlespace.domain.Coordinate;
import com.battlespace.service.Roller;

public interface BasicAttackPlan extends AttackPlan
{
    Map<Coordinate, Coordinate> selectAttack(Roller rng);
}
