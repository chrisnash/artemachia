package com.battlespace.service;

import com.battlespace.domain.AbsoluteStat;
import com.battlespace.domain.RangedStat;
import com.battlespace.domain.Stat;

public class StatFactory
{

    public static Stat create(String string)
    {
        // if the stat is a range x-y create a ranged one, otherwise create an absolute one
        int dash = string.indexOf('-');
        if(dash==-1)
        {
            return new AbsoluteStat(Double.valueOf(string));
        }
        else
        {
            String p1 = string.substring(0, dash);
            String p2 = string.substring(dash + 1);
            return new RangedStat(Double.valueOf(p1), Double.valueOf(p2));
        }
    }
    
    public static Stat create(double v1, double v2) throws Exception
    {
        if((v1>v2)||(v1<0.0)) throw new Exception("Illegal stat range " + v1 + "-" + v2);
        if(v1==v2) return new AbsoluteStat(v1);
        return new RangedStat(v1, v2);
    }

}
