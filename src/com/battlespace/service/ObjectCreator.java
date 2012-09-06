package com.battlespace.service;

import java.lang.reflect.Constructor;
import java.util.List;

import com.battlespace.domain.FileData;

public class ObjectCreator
{
    public static Object createObject(String pack, String className, List<String> params) throws Exception
    {
        Class<?> clazz = (Class<?>) Class.forName(pack + "." + className);
        Constructor<?> constructor = clazz.getConstructor(List.class);
        return constructor.newInstance(params);
    }
    
    public static Object createObjectFromList(String pack, List<String> params, int offset) throws Exception
    {
        String clazz = params.get(offset);
        List<String> extra = params.subList(offset+1, params.size());
        return createObject(pack, clazz, extra);
    }
    
    public static Object createObjectFromConfig(String pack, FileData fd, String key) throws Exception
    {
        return createObjectFromConfig(pack, fd, key, 0);
    }
    
    public static Object createObjectFromConfig(String pack, FileData fd, String key, int offset) throws Exception
    {
        List<String> params = fd.getList(key);
        return createObjectFromList(pack, params, offset);
    }

}
