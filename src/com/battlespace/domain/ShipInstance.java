package com.battlespace.domain;

import java.util.List;

public interface ShipInstance
{
    List<Stat> getEffectiveStats() throws Exception;

    Ship getParent();

    double getEffectiveCount();

    boolean isAlive();

    void updateDamage(DamageEntry damageEntry) throws Exception;

    int getUnits();

    Stat getDamage();

    void setDamage(double d) throws Exception;
}
