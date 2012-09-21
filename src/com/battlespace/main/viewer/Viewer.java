package com.battlespace.main.viewer;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;

public interface Viewer
{
    void beginTurn(Deployment player, Deployment enemy);
    void recordDamage(Deployment victim, Coordinate target, double damage);
    void endTurn(Deployment player, Deployment enemy);
}
