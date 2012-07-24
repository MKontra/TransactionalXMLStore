/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ModelInfo
{

    private static Map<Class, ModelInfo> modelInfos = new ConcurrentHashMap<Class, ModelInfo>();
    private Class forClass;
    private Field pKeyField;
    private Class pKeyType;
    private List<Field> allFields;
    private Map<String, Field> nameToField;

    public static ModelInfo getModelInfoFor(Class c)
    {
        ModelInfo existing = modelInfos.get(c);
        if (existing != null)
        {
            return existing;
        } else
        {
            ModelInfo newInfo = new ModelInfo(c);
            modelInfos.put(c, newInfo);
            return newInfo;
        }
    }

    public static Object getProxyFor(Class c) 
    {
        try
        {
            return c.getConstructor().newInstance();
        } catch (Exception ex)
        {
            Logger.getLogger(ModelInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private ModelInfo(Class c)
    {
        forClass = c;
        pKeyField = EntityReflectUtils.getPKeyField(c);
        pKeyType = EntityReflectUtils.getPKeyClass(c);

        allFields = EntityReflectUtils.getAllFields(new LinkedList<Field>(), c);
        nameToField = new HashMap<String, Field>();
        for ( Field f : allFields)
        {
            nameToField.put(f.getName(), f);
        }
    }

    public Field getFieldForName(String fname)
    {
        return nameToField.get(fname);
    }
    
    public boolean areDifferent(Object o1, Object o2)
    {
        for (Field f : allFields)
        {
            try
            {
                Object ref1 = f.get(o1);
                Object ref2 = f.get(o2);
                if ( ref1.equals(ref2) )
                {
                    return false;
                }
            } catch (Exception e)
            {
                Logger.getLogger(ModelInfo.class.getName()).log(Level.SEVERE, null, e);
                return false;
            }
        }
        return true;
    }

    public Class getForClass()
    {
        return forClass;
    }

    public List<Field> getAllFields()
    {
        return allFields;
    }

    
    
    public Field getpKeyField()
    {
        return pKeyField;
    }

    public Class getpKeyType()
    {
        return pKeyType;
    }
    

    public Object getClassInstance()
    {
        try
        {
            return forClass.getConstructor().newInstance();
        } catch (Exception ex)
        {
            Logger.getLogger(ModelInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
