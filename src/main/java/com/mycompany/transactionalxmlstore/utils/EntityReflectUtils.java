/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import com.mycompany.transactionalxmlstore.XMLStoreSet;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 *
 * @author Administrator
 */
public final class EntityReflectUtils
{

    public static Field getPKeyField(Class c)
    {
        for (Field f : getAllFields(new LinkedList<Field>(), c))
        {
            Id annotation = f.getAnnotation(javax.persistence.Id.class);
            if (annotation != null)
            {
                return f;
            }
        }
        return null;
    }

    private EntityReflectUtils()
    {
    }

    public static Class getPKeyClass(Class c)
    {
        for (Field f : getAllFields(new LinkedList<Field>(), c))
        {
            Id annotation = f.getAnnotation(javax.persistence.Id.class);
            if (annotation != null)
            {
                return f.getType();
            }
        }
        return null;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type)
    {
        for (Field field : type.getDeclaredFields())
        {
            fields.add(field);
        }

        if (type.getSuperclass() != null)
        {
            fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
    
    public static void setEntityValuesTo(Object dst, Object src)
    {
        if ( dst.getClass() != src.getClass() )
        {
            Logger.getLogger(EntityReflectUtils.class.getName()).log(Level.SEVERE, null, new Exception("Copying distinct objects"));
        }
        List<Field> allFieldsDst = getAllFields(new LinkedList<Field>(), dst.getClass());
        List<Field> allFieldsSrc = getAllFields(new LinkedList<Field>(), src.getClass());
        for (Field f : allFieldsDst)
        {
            try
            {
                f.setAccessible(true);
                f.set(dst, src.getClass().getDeclaredField(f.getName()).get(src));
            } catch (Exception ex)
            {
                Logger.getLogger(EntityReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
}
