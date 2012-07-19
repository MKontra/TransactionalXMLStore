/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils.classvisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ClassVisitor
{
    private Class targetClass;
    
    private IConstructorAction ica;
    private IFieldAction ifa;
    private IMethodAction ima;
    private IScopeManagement ism;
    
    private LinkedList<Class> toInspect;
    private Map<Class, Boolean> visited;
    
    public ClassVisitor(Class targetClass)
    {
        this.targetClass = targetClass;
        toInspect = new LinkedList<Class>();
        visited = new HashMap<Class, Boolean>();
    }

    public void visit()
    {
        visit(10000L);
    }
    
    private void visit(long l)
    {
        long depth = 0;
        toInspect.offer(targetClass);
        while ( !toInspect.isEmpty() )
        {
            Class processed = toInspect.poll();
            if (this.ica != null)
                for (Constructor c : processed.getConstructors())
                {
                    ica.processConstructor(c);
                }
            
            if (this.ima != null)
                for (Method m : processed.getMethods())
                {
                    ima.processMethod(m);
                }
            
            if (this.ifa != null)
                for (Field m : processed.getFields())
                {
                    ifa.processField(m);
                }
            
            
            
        }
        
    }
    
    public void registerFieldAction(IFieldAction ifa)
    {
        this.ifa = ifa;
    }
    
    public void registerAllActions(BaseAbstractProcessor bac)
    {
        this.ica = bac;
        this.ifa = bac;
        this.ima = bac;
        this.ism = bac;
    }

}
