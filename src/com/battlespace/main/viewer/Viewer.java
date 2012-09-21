package com.battlespace.main.viewer;

import java.util.List;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;

public interface Viewer
{
    void beginTurn(Deployment player, Deployment enemy);
    void recordDamage(Deployment victim, Coordinate target, double damage, List<Coordinate> attackers);
    void endTurn(Deployment player, Deployment enemy);
}
