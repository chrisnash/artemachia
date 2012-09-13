package com.battlespace.main;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.battlespace.domain.EnemyShip;
import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.PlanetData;
import com.battlespace.service.PlanetService;

public class DataValueEnemyFinder
{
    class DataValueRecord implements Comparable<DataValueRecord>
    {
       public String code;
       public double dv;

       public DataValueRecord(String code, double dv)
       {
           this.code = code;
           this.dv = dv;
       }

    @Override
        public int compareTo(DataValueRecord arg0)
        {
            double d = arg0.dv - this.dv;
            if(d==0.0)
            {
                return code.compareTo(arg0.code);
            }
            return (int)Math.signum(d);
        }
    
        public String toString()
        {
            return code + "(" + dv + ")";
        }
    }
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        new DataValueEnemyFinder().execute();
    }
    
    public void execute() throws Exception
    {
        EnemyShipDatabase esd = EnemyShipDatabase.load();
        
        Collection<EnemyShip> ships = esd.getAllShips();
        SortedSet<DataValueRecord> sorted = new TreeSet<DataValueRecord>();
        
        Map<String,Double> dvs = new HashMap<String,Double>();
        
        for(EnemyShip nme : ships)
        {
            double v = nme.durability.value(true) - nme.durability.value(false);
            sorted.add(new DataValueRecord(nme.getCode(), v));
        }
        for(DataValueRecord dv : sorted)
        {
            Set<PlanetData> planets = PlanetService.findShip(dv.code);
            System.out.println(dv + ":" + planets);
            dvs.put(dv.code, dv.dv);
        }
        
        // now get all planets and find the one with the biggest dv
        SortedSet<DataValueRecord> planets = new TreeSet<DataValueRecord>();
        Collection<PlanetData> ps = PlanetService.getAllPlanets();
        for(PlanetData p : ps)
        {
            String enemies = p.enemies;
            String[] split = enemies.split(",");
            Set<String> de = new HashSet<String>();
            for(String n : split)
            {
                de.add(n);
            }
            double dv = 0.0;
            for(String n : de)
            {
                dv += dvs.get(n);
            }
            planets.add(new DataValueRecord(p.toString(), dv));
        }
        
        System.out.println();
        
        for(DataValueRecord dv : planets)
        {
            System.out.println(dv);
        }
    }

}
