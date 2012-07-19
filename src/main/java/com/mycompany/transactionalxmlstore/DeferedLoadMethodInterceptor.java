/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.EntityReflectUtils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 *
 * @author Administrator
 */
public class DeferedLoadMethodInterceptor implements MethodInterceptor 
{

    private Object pKey;

    public Object getpKey()
    {
        return pKey;
    }
    
    private XMLStoreSet store;
    
    private boolean initialized = false;
    
    public DeferedLoadMethodInterceptor(XMLStoreSet store, Object objectPKey)
    {
        this.pKey = objectPKey;
        this.store = store;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] os, MethodProxy mp) throws Throwable
    {
        //synchronized ?
        if ( !initialized )
        {
            EntityReflectUtils.setEntityValuesTo(o, store.Load(pKey));
            initialized = true;
        }
        return mp.invokeSuper(o, os);
    }
    
}
