package com.battlespace.domain;

import java.util.List;

public interface Ship
{
    int getUnits();

    List<Stat> getSummaryStats();
    List<Stat> getShieldStats();

    String getName();

    Stat getTorpedoDamage(String size, double d) throws Exception;

    Stat getPlasmaDamage(String size, double d) throws Exception;

    String getSize();

    Stat getDurability();

    Stat getDomination();

    int getReplacementTime();

    double dataValue();

    void updateDataValue(double d);
}
