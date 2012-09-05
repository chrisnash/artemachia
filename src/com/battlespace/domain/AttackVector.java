package com.battlespace.domain;

import java.util.List;

public class AttackVector
{
    int line;                   // which line is being attacked
    Integer deviation;          // how many rows off
    List<Coordinate> targets;   // target list
    
    public AttackVector(int line, Integer deviation, List<Coordinate> targets)
    {
        this.line = line;
        this.deviation = deviation;
        this.targets = targets;
    }
    
    public String toString()
    {
        if(deviation==null) return "";
        return "L"+line+"D"+deviation+":"+targets;
    }
}
