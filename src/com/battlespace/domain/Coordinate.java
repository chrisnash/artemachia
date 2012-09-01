package com.battlespace.domain;

public class Coordinate implements Comparable<Coordinate>
{
    int r;
    int c;
    
    public Coordinate(int r, int c)
    {
        this.r = r;
        this.c = c;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + c;
        result = prime * result + r;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (c != other.c)
            return false;
        if (r != other.r)
            return false;
        return true;
    }

    @Override
    public int compareTo(Coordinate c2)
    {
        if(this.r != c2.r)
        {
            return this.r - c2.r;
        }
        return this.c - c2.c;
    }
}
