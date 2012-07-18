/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author Administrator
 */
public class XMLStoreSet<T>
{

    private XMLStoreContext context;
    private File f;
    private Class forcls;
    
    private enum pKeyType 
    {
        Stringpkey, longpkey, hashpkey
    }
    
    private pKeyType pKtyp;
    private SequenceGenerator pKeyGen;
    
    private Map<Object, File> trackingMap;
    private Map<Object, Object> oldStateMap;

    XMLStoreSet(XMLStoreContext xsc, Class forcls, File f)
    {
        this.forcls = forcls;
        this.f = f;
        this.context = xsc;
        
        
        
    }

    public void Load(Object pkey)
    {
        
    }
    
    public void Add(T obj)
    {
    }
}
