package com.battlespace.domain;

import java.util.LinkedList;
import java.util.List;

public class DamageEntry
{
    Stat damage;
    int remainingShips;
    
    public DamageEntry(String value, int remaining)
    {
        this.damage = RangedStat.statFromDisplay(value);
        this.remainingShips = remaining;
    }
    
    public static List<DamageEntry> parseDamage(List<String> pieces, Deployment d) throws Exception
    {
        // first five entries are front row
        // next five are back row
        List<DamageEntry> frontRow = new LinkedList<DamageEntry>();
        List<DamageEntry> backRow = new LinkedList<DamageEntry>();
        
        if(pieces.size() != 5) throw new Exception("Damage string incorrect format " + pieces);
        int fr = d.frontLine();
        for(int i=0;i<5;i++)
        {
            String piece = pieces.get(i);
            // ignore the critical marker for the moment
            if(piece.startsWith("C"))
            {
                piece = piece.substring(1);
            }
            boolean frship = (d.getLivingShip(i,fr) != null);
            boolean brship = (d.getLivingShip(i, fr+1) != null);
            if((!frship)&&(!brship))
            {
                if(!piece.equals("")) throw new Exception("Damage string " + piece + " on empty row " + i);
                frontRow.add(null);
                backRow.add(null);
            }
            else if(frship && brship)
            {
                String[] parts = piece.split("\\/");
                if(parts.length!=2) throw new Exception("Two damage entries expected on row " + i + " but got " + piece);
                frontRow.add(pieceParse(parts[0]));
                backRow.add(pieceParse(parts[1]));
            }
            else
            {
                if(frship)
                {
                    frontRow.add(pieceParse(piece));
                    backRow.add(null);
                }
                else
                {
                    frontRow.add(null);
                    backRow.add(pieceParse(piece));
                }
            }
        }
        List<DamageEntry> rv = new LinkedList<DamageEntry>();
        rv.addAll(frontRow);
        rv.addAll(backRow);
        return rv;
    }

    private static DamageEntry pieceParse(String piece) throws Exception
    {
        if(piece.equals("")) return null;   // empty string = no damage
        int open = piece.indexOf("(");
        int close = piece.indexOf(")");
        if((open==-1)||(close==-1)||(open>close)) throw new Exception("Not a valid damage string " + piece);
        return new DamageEntry(piece.substring(0,open), Integer.valueOf(piece.substring(open+1,close)) );
    }
    
}
