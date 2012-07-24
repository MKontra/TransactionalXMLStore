/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.xadisk.additional.XAFileOutputStreamWrapper;
import org.xadisk.bridge.proxies.interfaces.Session;

/**
 *
 * @author Administrator
 */
public class XMLStoreSet<T>
{

    private XMLStoreContext xsc;
    private File location;

    public XMLStoreSet(XMLStoreContext xsc, File loc)
    {
        this.xsc = xsc;
        this.location = loc;
    }
    
    public void add(T obj)
    {
        xsc.addToStore(obj, location);
    }
    
    public T load(Object pKey)
    {
        return (T) xsc.loadFromStore(pKey, location);
    }
    
    public List<T> loadByAttribute(String name, Object value)
    {
        return null;
    }
    
    public void remove(Object pKey)
    {
        
    }
    
}
/**
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

    private Object getEntityForFile(InputStream f)
    {
        return null;
    }

    private void storeEntityToStream(Object o, OutputStream os)
    {
        try
        {
            JAXBContext jaxbcntx = JAXBContext.newInstance( forcls );
            Marshaller entityMarshaller = jaxbcntx.createMarshaller();
            context.plugContextAdaptersForReferences(forcls, pKeyField, entityMarshaller);
            //context.validateRestraintsFor(forcls, o);
            JAXBElement jx = new JAXBElement(new QName(forcls.getSimpleName()), forcls, o);
            entityMarshaller.marshal(jx, os);    
        } catch (JAXBException ex)
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
        Session cntxSession = context.getXaDiskSession();
        IFileOperations fhandler = cntxSession == null ? new FileToIFileOperations() : new XADiskSessionToIFileOperations(cntxSession);
        Object tme = oldStateMap.get(pkey);
        if (tme != null)
        {
            return (T) tme;
        } else
        {
            File entityFile = new File(f.getPath() + File.separator + PrimaryKeyUtils.pKeyToFileName(pkey));
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
        Session cntxSession = context.getXaDiskSession();
        IFileOperations fhandler = cntxSession == null ? new FileToIFileOperations() : new XADiskSessionToIFileOperations(cntxSession);
        File toAdd = new File(f.getPath() + File.separator + PrimaryKeyUtils.pKeyToFileName(PrimaryKeyUtils.getPrimaryKeyValue(this.pKeyField, obj)));
        if (fhandler.fileExists(toAdd, false))
        {
            throw new Exception("PKey already present");
        }

        fhandler.createFile(toAdd, false);

        OutputStream os = fhandler.createFileOutputStream(toAdd);
        
        storeEntityToStream(obj, os);
        
        oldStateMap.put(PrimaryKeyUtils.getPrimaryKeyValue(this.pKeyField, obj), obj);
        trackingMap.put(PrimaryKeyUtils.getPrimaryKeyValue(this.pKeyField, obj), toAdd);
        
        os.close();
    }
    
    public void Remove(T obj)
    {
        
    }
    
}
**/