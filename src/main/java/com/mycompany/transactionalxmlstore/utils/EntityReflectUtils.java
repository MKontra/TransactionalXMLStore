/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Administrator
 */
public final class EntityReflectUtils
{

    public static Field getPKeyField(Class c)
    {
        for ( Field f: getAllFields(new LinkedList<Field>(), c))
        {
            Id annotation = f.getAnnotation(javax.persistence.Id.class);
            if ( annotation != null )
                return f;
        }
        return null;
    }

    private EntityReflectUtils()
    {
    }
  
    public static Class getPKeyClass(Class c)
    {
        for ( Field f: getAllFields(new LinkedList<Field>(), c))
        {
            Id annotation = f.getAnnotation(javax.persistence.Id.class);
            if ( annotation != null )
                return f.getType();
        }
        return null;
    }
    
public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
    for (Field field: type.getDeclaredFields()) {
        fields.add(field);
    }

    if (type.getSuperclass() != null) {
        fields = getAllFields(fields, type.getSuperclass());
    }

    return fields;
}
    
}
