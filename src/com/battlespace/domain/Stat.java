package com.battlespace.domain;

public interface Stat
{
    Stat enhance(double percent);

    double value();
    
    double value(boolean max);
}
