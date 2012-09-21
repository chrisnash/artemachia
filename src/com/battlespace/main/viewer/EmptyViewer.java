package com.battlespace.main.viewer;

import java.util.List;

import com.battlespace.domain.Coordinate;
import com.battlespace.domain.Deployment;

public class EmptyViewer implements Viewer
{
    @Override
    public void beginTurn(Deployment player, Deployment enemy)
    {
    }

    @Override
    public void recordDamage(Deployment victim, Coordinate target, double damage, List<Coordinate> attackers)
    {
    }

    @Override
    public void endTurn(Deployment player, Deployment enemy)
    {
    }

}
