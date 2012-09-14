package com.battlespace.domain;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Formation
{
    SortedSet<Coordinate> coords = new TreeSet<Coordinate>();
    
    public static final int ROWS = 5;
    public static final int COLUMNS = 7;
    
    public Formation(String value) throws Exception
    {        
        if(value.length()!=ROWS*COLUMNS) throw new Exception("Invalid formation string length " + value.length());
        int index=0;
        for(int r=0; r<ROWS; r++)
        {
            for(int c=0;c<COLUMNS;c++)
            {
                String q = value.substring(index,index+1);
                if(q.equals("X"))
                {
                    coords.add(new Coordinate(r,c));
                }
                else if(!q.equals("."))
                {
                    throw new Exception("Invalid formation character " + q);
                }
                index++;
            }
        }
    }

    public Deployment deploy(List<? extends ShipInstance> ships) throws Exception
    {
        SortedMap<Coordinate, ShipInstance> deployData = new TreeMap<Coordinate, ShipInstance>();
        int index = 0;
        if(coords.size() != ships.size())
        {
            throw new Exception("Deployment mismatch formation=" + coords.size() + " ships=" + ships.size());
        }
        for(Coordinate c : coords)
        {
            ShipInstance si = ships.get(index++);
            si.getParent().clearDataValue();
            deployData.put(c, si);
        }
        return new Deployment(deployData);
    }

    public int shipCount()
    {
        return coords.size();
    }

}
