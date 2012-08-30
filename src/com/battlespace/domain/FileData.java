package com.battlespace.domain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileData
{
    Map<String, List<String> > items = new HashMap<String, List<String>>();

    public void add(String key, String[] values)
    {
        items.put(key, Arrays.asList(values));
    }

    public int getInt(String key, int index)
    {
        return Integer.valueOf(get(key, index));
    }

    public String get(String key, int i)
    {
        return getList(key).get(i);
    }

    public List<String> getList(String key)
    {
        return items.get(key);
    }

    public String get(String key)
    {
        return get(key, 0);
    }
    
    public Set<String> getKeys()
    {
        return items.keySet();
    }

}
