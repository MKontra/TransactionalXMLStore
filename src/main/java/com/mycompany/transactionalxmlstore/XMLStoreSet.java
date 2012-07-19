/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.EntityReflectUtils;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.SequenceGenerator;
import org.xadisk.additional.XAFileOutputStreamWrapper;

/**
 *
 * @author Administrator
 */
public class XMLStoreSet<T>
{

    private XMLStoreContext context;
    private File f;
    private Class forcls;
    private File pKeyIndex;
    private Class pKeyClass;
    private Field pKeyField;
    private SequenceGenerator pKeyGen;
    private Map<Object, File> trackingMap = new HashMap<Object, File>();
    private Map<Object, Object> oldStateMap = new HashMap<Object, Object>();

    private static String pKeyToFileName(Object pKey)
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

    private Object getPrimaryKeyValue(T obj)
    {
        try
        {
            return this.pKeyField.get(obj);
        } catch (IllegalArgumentException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Object getEntityForFile(InputStream f)
    {
        return null;
    }

    private void storeEntityToStream(Object o, OutputStream os)
    {
        try
        {
            os.write('A');
        } catch (IOException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    XMLStoreSet(XMLStoreContext xsc, Class forcls, File f) throws Exception
    {
        this.forcls = forcls;
        this.f = f;
        this.context = xsc;

        this.pKeyField = EntityReflectUtils.getPKeyField(forcls);
        if ( this.pKeyField == null )
            throw new Exception("No primary key specified");
        this.pKeyField.setAccessible(true);
        this.pKeyClass = pKeyField.getType();

        this.pKeyIndex = new File(f.getPath() + File.separator + forcls.getSimpleName() + "_pKeyIndex");
        try
        {
            pKeyIndex.createNewFile();
        } catch (IOException ex)
        {
            Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, "Failed opening index file", ex);
        }

    }

    public T Load(Object pkey)
    {
        Object tme = oldStateMap.get(pkey);
        if (tme != null)
        {
            return (T) tme;
        } else
        {
            File entityFile = new File(f.getPath() + File.separator + pKeyToFileName(pkey));
            if (!entityFile.exists())
            {
                return null;
            }

            T fentity = null;
            try
            {
                fentity = (T) getEntityForFile(new FileInputStream(entityFile));
            } catch (FileNotFoundException ex)
            {
                Logger.getLogger(XMLStoreSet.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            oldStateMap.put(pkey, fentity);
            trackingMap.put(pkey, entityFile);
            return fentity;
        }
    }

    public void Add(T obj) throws Exception
    {
        File toAdd = new File(f.getPath() + File.separator + pKeyToFileName(getPrimaryKeyValue(obj)));
        if (toAdd.exists())
        {
            throw new Exception("PKey already present");
        }

        toAdd.createNewFile();

        OutputStream os = null;
        if ( context.getXaDiskSession() != null )
        {
            os = new XAFileOutputStreamWrapper(context.getXaDiskSession().createXAFileOutputStream(toAdd, false));
        } else
        {
            os = new FileOutputStream(toAdd);
        }
        
        storeEntityToStream(obj, os);
        
        oldStateMap.put(getPrimaryKeyValue(obj), obj);
        trackingMap.put(getPrimaryKeyValue(obj), toAdd);
    }
    
    public void Remove(T obj)
    {
        
    }
    
}
