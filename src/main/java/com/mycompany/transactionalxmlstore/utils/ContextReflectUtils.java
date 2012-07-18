/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import com.mycompany.transactionalxmlstore.XMLStoreContext;
import com.mycompany.transactionalxmlstore.XMLStoreSet;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public final class ContextReflectUtils {

    private ContextReflectUtils() {
    }

    public static List<Field> listAllSetFieldsFor(Class xsc) {
        Field[] declaredFields = xsc.getDeclaredFields();
        List<Field> sets = new LinkedList<Field>();
        for (Field f : declaredFields) {
            if (XMLStoreSet.class.isAssignableFrom( f.getType() ))  {
                sets.add(f);
            }
        }

        return sets;

    }
    private static List<Class<?>> getAllContextModels(Class<?> c)
    {
        List<Field> allSetsForThis = ContextReflectUtils.listAllSetFieldsFor(c);
        List<Class<?>> retval = new LinkedList<Class<?>>();

        for (Field f : allSetsForThis)
        {
            retval.add(ContextReflectUtils.getXMLStoreSetContainedType(f));
        }

        return retval;
    }
    public static Class getXMLStoreSetContainedType(Field f) {
        return (Class) ((ParameterizedType) f.getGenericType() ).getActualTypeArguments()[0];
    }
}
