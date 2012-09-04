package com.battlespace.service;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.battlespace.domain.AbstractPlayerPower;
import com.battlespace.domain.Booster;
import com.battlespace.domain.CommanderPower;
import com.battlespace.domain.FileData;
import com.battlespace.domain.PlayerShip;
import com.battlespace.domain.PlayerShipInstance;

public class PlayerSkillModifier
{
    static SortedSet<AbstractPlayerPower> _pp;
    
    public static void upgrade(Booster booster,
            int militarySkill) throws Exception
    {
        SortedSet<AbstractPlayerPower> pp = loadDatabase();
        for(AbstractPlayerPower app : pp)
        {
            if(app.level > militarySkill) break;
            app.upgrade(booster);
        }
    }

    private static SortedSet<AbstractPlayerPower> loadDatabase() throws Exception
    {
        if(_pp==null)
        {
            _pp = new TreeSet<AbstractPlayerPower>();
            
            FileData fd = DataLoaderService.loadFile("data/military_bonuses.txt");
            Set<String> keys = fd.getKeys();
            for(String key : keys)
            {
                List<String> data = fd.getList(key);
                int level = Integer.valueOf(data.get(0));
                String name = data.get(1);
                String className = data.get(2);
                List<String> params = data.subList(3, data.size());
                
                Class<? extends AbstractPlayerPower> clazz = (Class<? extends AbstractPlayerPower>) Class.forName("com.battlespace.domain.player." + className);
                Constructor<? extends AbstractPlayerPower> constructor = clazz.getConstructor(List.class);
                AbstractPlayerPower cp = constructor.newInstance(params);
                cp.level = level;
                cp.name = name;
                _pp.add(cp);
            }
        }
        return _pp;
    }

}
