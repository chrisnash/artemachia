package com.battlespace.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AttackOptions
{
    AttackVector front;
    AttackVector rear;

    public AttackOptions(AttackVector f, AttackVector r)
    {
        this.front = f;
        this.rear = r;
    }
    
    public String toString()
    {
        return this.front+"/"+this.rear;
    }

    public Collection<Coordinate> getAllTargets()
    {
        List<Coordinate> out = new LinkedList<Coordinate>();
        out.addAll(front.targets);
        out.addAll(rear.targets);
        return out;
    }
}
