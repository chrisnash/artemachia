package com.battlespace.domain;

import java.util.List;

public interface ShipInstance
{
    List<Stat> getEffectiveStats() throws Exception;
}
