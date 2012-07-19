/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils;

import com.mycompany.transactionalxmlstore.DeferedLoadMethodInterceptor;
import com.mycompany.transactionalxmlstore.XMLStoreSet;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public final class PrimaryKeyUtils
{
    public static String pKeyToFileName(Object pKey)
    {
        if (pKey.getClass() == long.class)
        {
            return String.valueOf((Long) pKey);
        } else
        {
            if (pKey.getClass() == String.class)
            {
                return (String) pKey;
            } else
            {
                if (pKey.getClass() == int.class)
                {
                    return String.valueOf((Integer) pKey);
                }
            }
        }
        return pKey.toString();
    }

    public static Object getPrimaryKeyValue(Field f, Object obj)
    {
        try
        {
            if ( Proxy.isProxyClass (obj.getClass()))
            {
                try
                {
                    return ((DeferedLoadMethodInterceptor) Proxy.getInvocationHandler(obj)).getpKey();
                } catch (ClassCastException classCastException)
                {
                    Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, classCastException);
                    return null;
                }
            }
            return f.get(obj);
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private PrimaryKeyUtils()
    {
    }
    
}
