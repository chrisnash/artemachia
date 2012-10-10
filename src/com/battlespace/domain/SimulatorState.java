package com.battlespace.domain;

public interface SimulatorState
{
    void addResult(SimulatorResults r) throws Exception;

    double getStat(String string);
}
