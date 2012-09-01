package com.battlespace.domain;

import java.util.List;
import java.util.SortedSet;
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
                String q = value.substring(c,c+1);
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

    public Deployment deploy(List<? extends ShipInstance> attackShips)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
