/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.ContextReflectUtils;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xadisk.bridge.proxies.interfaces.*;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;
import org.xadisk.filesystem.standalone.StandaloneFileSystemConfiguration;

/**
 *
 * @author Administrator
 */
public class XMLStoreContext
{

    private static Properties staticConfig = null;
    private static XAFileSystem xaf = null;
    private static Map<File, XMLStoreContext> contextMap = new ConcurrentHashMap<File, XMLStoreContext>();

    static
    {
        InputStream is = null;
        Properties prop = new Properties();
        staticConfig = prop;
        try
        {

            URL confurl = ClassLoader.getSystemResource("XMLStoreConfig.xml");
            if (confurl != null)
            {
                is = confurl.openStream();
                prop.loadFromXML(is);
            }


        } catch (IOException ex)
        {
            Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, "Could not load properties", ex);
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } catch (Throwable ex)
            {
                Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, "Error closing property stream", ex);
            }
        }

        String instanceName = staticConfig.getProperty("XADiskInstanceName", "defaultXADiskSystemLocation");
        String XADiskSystemLocation = staticConfig.getProperty("XADiskSystemLocation", ".") + File.separator + staticConfig.getProperty("APPName", "defaultXADiskInstance");

        StandaloneFileSystemConfiguration configuration =
                new StandaloneFileSystemConfiguration(XADiskSystemLocation, instanceName);


        xaf = XAFileSystemProxy.bootNativeXAFileSystem(configuration);
        try
        {
            xaf.waitForBootup(10000L);
        } catch (InterruptedException ex)
        {
            xaf = null;
            Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private File store;
    private Map<Field, File> modelMapping;
    private org.xadisk.bridge.proxies.interfaces.Session xaDiskSession;

    public Session getXaDiskSession()
    {
        return xaDiskSession;
    }

    private Class<?> getDerivingClass()
    {
        return this.getClass();
    }

    private class DefaultProperties extends Properties
    {

        public DefaultProperties()
        {
            this.setProperty("XMLStoreLocation", getDerivingClass().getSimpleName());
        }
    }

    private class DelegatingProperties extends Properties
    {

        Properties fwding = null;
        Properties defaultInstance = new DefaultProperties();

        public DelegatingProperties(Properties defaults)
        {
            this.fwding = defaults;
        }

        @Override
        public String getProperty(String key)
        {
            return fwding.getProperty(key, defaultInstance.getProperty(key));
        }

        @Override
        public String getProperty(String key, String defaultValue)
        {
            return fwding.getProperty(key, defaultInstance.getProperty(key, defaultValue));
        }
    }
    private Properties config = new Properties(new DefaultProperties());

    private Map<Field, File> prepareModelMapping(List<Field> models)
    {
        Map<Field, File> retval = new ConcurrentHashMap<Field, File>();
        for (Field f : models)
        {
            File checkedDir = new File(this.config.getProperty("XMLStoreLocation") + File.separator +f.getName());
            retval.put(f, checkedDir);
        }
        return retval;
    }



    private static void chceckAndCreateDictionaryStructure(Map<Field, File> mapping)
    {
        for (File f : mapping.values())
        {
            if (!f.exists())
            {
                f.mkdirs();
            }
        }
    }

    private static File chcekStorePresence(Set<File> locations, File thisloc)
    {
        for (File f : locations)
        {
            if (f.getAbsolutePath().equals(thisloc.getAbsolutePath()))
            {
                return f;
            }
        }
        return null;
    }

    public XMLStoreContext()
    {
        this.initializeStore();
    }

    public XMLStoreContext(String location)
    {
        this.config.setProperty("XMLStoreLocation", location);
        this.initializeStore();
    }

    public XMLStoreContext(Properties config)
    {
        this.config = new DelegatingProperties(config);
        this.initializeStore();
    }

    private void initializeStore()
    {
        File stloc = new File(this.config.getProperty("XMLStoreLocation"));
        Class<? extends XMLStoreContext> aClass = this.getClass();
        System.out.println(aClass.getSimpleName());
        File storeLoc = chcekStorePresence(contextMap.keySet(), stloc);
        if (storeLoc != null)
        {
            this.store = storeLoc;
        } else
        {
            modelMapping = prepareModelMapping(ContextReflectUtils.listAllSetFieldsFor(this.getClass()));
            chceckAndCreateDictionaryStructure(modelMapping);

            List<Field> classFields = ContextReflectUtils.listAllSetFieldsFor(this.getClass());
            for (Field f : classFields)
            {
                try
                {
                    XMLStoreSet inst = 
                            (XMLStoreSet) XMLStoreSet.class.getDeclaredConstructor(
                            XMLStoreContext.class, 
                            Class.class,
                            File.class).newInstance(
                            this, 
                            ContextReflectUtils.getXMLStoreSetContainedType(f),
                            modelMapping.get(f));
                    f.setAccessible(true);
                    f.set(this, inst );
                    f.setAccessible(false);
                } catch (InstantiationException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex.getTargetException());
                } catch (IllegalArgumentException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            contextMap.put(stloc, this);



        }
    }

    public void rollbackTransaction()
    {
        synchronized (this)
        {
            if (this.xaDiskSession != null)
            {
                try
                {
                    xaDiskSession.rollback();
                } catch (NoTransactionAssociatedException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.xaDiskSession = null;
            }
        }
    }

    public void commitTransaction()
    {
        synchronized (this)
        {
            if (this.xaDiskSession != null)
            {
                try
                {
                    xaDiskSession.commit();
                } catch (NoTransactionAssociatedException ex)
                {
                    Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.xaDiskSession = null;
            }
        }
    }

    public void beginTransaction()
    {
        synchronized (this)
        {
            if (this.xaDiskSession == null)
            {
                xaDiskSession = xaf.createSessionForLocalTransaction();
            }
        }
    }

    public void close()
    {
        contextMap.remove(this.store);
    }

    @Override
    protected void finalize() throws Throwable
    {
        this.close();
        super.finalize();
    }
}
