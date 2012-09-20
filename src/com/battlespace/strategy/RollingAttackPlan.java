package com.battlespace.strategy;

import java.util.Collection;
import java.util.SortedSet;

import com.battlespace.domain.Coordinate;
import com.battlespace.service.Roller;

// an attack plan that's dynamic. A rolling plan will process attacking ships in a particular order
// and if a defender is destroyed, later ships won't shoot at it.
public interface RollingAttackPlan extends AttackPlan
{
    SortedSet<Coordinate> getAttackerOrder();

    Coordinate selectSingleAttack(Coordinate attacker,
            Collection<Coordinate> survivors, Roller rng);
}
