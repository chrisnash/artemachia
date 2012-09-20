package com.battlespace.main.parsers;

import com.battlespace.domain.PlanetData;
import com.battlespace.service.PlanetService;

public class ParsedEnemy
{
    public boolean interception;
    public String enemyFormation;
    public String enemyShips;
    
    public ParsedEnemy(String arg) throws Exception
    {
        // param 2 is enemy selection
        // (planet)
        // (planet level),(config)
        // (formation),(ship,ship,ship...)
        // i,(formation),(ship,ship,ship....)
        interception = false;
        String[] enemyArgs = arg.split(",");
        enemyFormation = null;
        enemyShips = null;
        if(enemyArgs.length==1)
        {
            PlanetData pd = PlanetService.lookup(enemyArgs[0]);
            enemyFormation = pd.formation;
            enemyShips = pd.enemies;
        }
        else if(enemyArgs.length==2)
        {
            PlanetData pd = PlanetService.lookupByLayout(Integer.valueOf(enemyArgs[0]), enemyArgs[1]);
            enemyFormation = pd.formation;
            enemyShips = pd.enemies;
            System.out.println("Planet code is " + pd.code);
        }
        else
        {
            int base = 0;
            if(enemyArgs[0].startsWith("i"))
            {
                base++;
                interception = true;
            }
            // formation, ship, ship, ship....
            enemyFormation = enemyArgs[base];
            StringBuffer sb = new StringBuffer();
            for(int i=base+1;i<enemyArgs.length;i++)
            {
                if(i!=base+1) sb.append(",");
                sb.append(enemyArgs[i]);
            }
            enemyShips = sb.toString();
        }
    }

}
