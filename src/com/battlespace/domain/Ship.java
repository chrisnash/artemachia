package com.battlespace.domain;

import java.util.List;

public interface Ship
{
    int getUnits();

    List<Stat> getSummaryStats();

    String getName();
}
