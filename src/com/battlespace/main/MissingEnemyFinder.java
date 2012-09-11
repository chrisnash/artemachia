package com.battlespace.main;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.battlespace.domain.EnemyShipDatabase;
import com.battlespace.domain.PlanetData;
import com.battlespace.service.PlanetService;

public class MissingEnemyFinder
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        EnemyShipDatabase esd = EnemyShipDatabase.load();
        Set<String> missing = new HashSet<String>();
        Set<PlanetData> planetsToVisit = new HashSet<PlanetData>();
        
        Collection<PlanetData> allPlanets = PlanetService.getAllPlanets();
        for(PlanetData p : allPlanets)
        {
            String e = p.enemies;
            String[] eList = e.split(",");
            for(String s : eList)
            {
                try
                {
                    esd.lookup(s);
                }
                catch(final Exception x)
                {
                    missing.add(s);
                    planetsToVisit.add(p);
                }
            }
        }
        System.out.println(missing);
        System.out.println(planetsToVisit);
    }

}
