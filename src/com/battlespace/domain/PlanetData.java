package com.battlespace.domain;

import java.util.Arrays;
import java.util.List;

public class PlanetData
{
    public String code;
    public int level;
    public String planetConfig;
    public String formation;
    public String enemies;
    
    public PlanetData(String code, int level, String planetConfig, String formation, List<String> enemies)
    {
        this.code = code;
        this.level = level;
        this.planetConfig = sanitize(planetConfig);
        this.formation = formation;
        StringBuffer sb = new StringBuffer();
        for(String enemy : enemies)
        {
            if(sb.length()!=0) sb.append(",");
            sb.append(enemy);
        }
        this.enemies = sb.toString();
    }
    
    public static String sanitize(String pconfig)
    {
        String[] characters = pconfig.split("");
        Arrays.sort(characters);
        StringBuffer sb = new StringBuffer();
        for(String c : characters)
        {
            sb.append(c);
        }
        return sb.toString();
    }
    
    public String toString()
    {
        return code + "," + level + ( (planetConfig.isEmpty())?(""):(","+planetConfig) );
    }
}
