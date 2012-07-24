/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import java.io.File;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xadisk.bridge.proxies.interfaces.Session;
import org.xadisk.bridge.proxies.interfaces.XAFileSystem;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 *
 * @author Administrator
 */
public class XMLStoreContext
{

    private static XMLStoreContextRegister registry = new XMLStoreContextRegister();
    private XMLStoreConfiguration instanceConfiguration = new XMLStoreConfiguration();
    
    private XAFileSystem xaf = XADiskInstanceManager.INSTANCE.getFileSystemInstance();
    
    private File contextLocation;
    private XMLStoreContextEntry xsce;
    
    private org.xadisk.bridge.proxies.interfaces.Session xaDiskSession;

    public Session getXaDiskSession()
    {
        return xaDiskSession;
    }
    
    private Class<?> getDerivingClass()
    {
        return this.getClass();
    }

    public XMLStoreContext()
    {
        this.instanceConfiguration.setLocation(getDerivingClass().getSimpleName());
        this.initializeStore();
    }

    public XMLStoreContext(String location)
    {
        this.instanceConfiguration.setLocation(location);
        this.initializeStore();
    }

    public XMLStoreContext(XMLStoreConfiguration config)
    {
        this.instanceConfiguration = config;
        this.initializeStore();
    }

    private void initializeStore()
    {
        contextLocation = new File(this.instanceConfiguration.getLocation());
        xsce = registry.registerContextForLocation(contextLocation, getDerivingClass());
        if (xsce.contextDerivedClass != getDerivingClass())
        {
            Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, new Exception("Context class invalid in location"));
            return;
        }
        
        for (Entry<File, XMLStoreSetEntry> ent : xsce.managedSets.entrySet())
        {
            try
            {
                XMLStoreSet inst =
                        (XMLStoreSet) XMLStoreSet.class.getDeclaredConstructor(
                        XMLStoreContext.class,
                        File.class).newInstance(
                        this,
                        ent.getKey());
                ent.getValue().declaredField.setAccessible(true);
                ent.getValue().declaredField.set(this, inst);
            } catch (Exception ex)
            {
                Logger.getLogger(XMLStoreContext.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
    
    
    
    public boolean beginTransaction()
    {
        if (xaDiskSession == null)
        {
            xaDiskSession = xaf.createSessionForLocalTransaction();
            return true;
        } else
        {
            return false;
        }
    }
    
    public boolean rollback() throws NoTransactionAssociatedException
    {
        if (xaDiskSession == null)
        {
            return false;
        } else
        {
            xaDiskSession.rollback();
            xaDiskSession = null;
            return true;
        }
    }
    
    public boolean commit() throws NoTransactionAssociatedException
    {
        if (xaDiskSession == null)
        {
            return false;
        } else
        {
            xaDiskSession.commit();
            xaDiskSession = null;
            return true;
        }
    }
    
    public void addToStore(Object o, File loc)
    {
        IXMLStoreContextOperationsProvider opprov = xsce.managedSets.get(loc).provider;
        if  (xaDiskSession != null ) opprov.bindToSession(xaDiskSession);
        opprov.add(o);
        opprov.unbindSession();
    }
    
    public Object loadFromStore(Object pKey, File loc)
    {
        IXMLStoreContextOperationsProvider opprov = xsce.managedSets.get(loc).provider;
        if  (xaDiskSession != null ) opprov.bindToSession(xaDiskSession);
        Object retval = opprov.load(pKey);
        opprov.unbindSession();
        return retval;
    }
    
}
    /**
public class XMLStoreContext
{

    private static Properties staticConfig = null;
    private static XAFileSystem xaf = null;
    private static List<XMLStoreContextEntry> initializedContexts = new LinkedList<XMLStoreContextEntry>();
    private static Map<Class, IXMLStoreContextFirstInstanceInitializer> refAdaptersInitializators = new DefaultedConcurrentHashMap<Class, IXMLStoreContextFirstInstanceInitializer>(new DefaultXMLStoreContextInitializer());
    private static Map<Class, XmlAdapter> refAdapters = new HashMap<Class, XmlAdapter>();


    private List<XMLStoreSetEntry> managedSets = new LinkedList<XMLStoreSetEntry>();

    private static interface IXMLStoreContextFirstInstanceInitializer
    {

        public void initializeClass(XMLStoreContext xsc);
    }

    private static class XMLStoreContextDoNothingInitializer implements IXMLStoreContextFirstInstanceInitializer
    {

        @Override
        public void initializeClass(XMLStoreContext xsc)
        {
            //Do Nothing
        }
    }

    private static class DefaultXMLStoreContextInitializer implements IXMLStoreContextFirstInstanceInitializer
    {

        @Override
        public void initializeClass(XMLStoreContext xsc)
        {
            synchronized (XMLStoreContext.class)
            {
                List<Field> classFields = ContextReflectUtils.listAllSetFieldsFor(this.getClass());
                xsc.refAdaptersInitializators.put(xsc.getClass(), new XMLStoreContextDoNothingInitializer());
            }
        }
    }

    static
    {


    }
    private File store;
    private Map<Field, File> modelMapping;
    private List<Class> mappedClasses;


    private Class<?> getDerivingClass()
    {
        return this.getClass();
    }

    void plugContextAdaptersForReferences(Class c, Field pKeyField, Marshaller entityMarshaller)
    {
        System.out.println("Plugging marshallers");
        for (Class pc : mappedClasses)
        {
            //if ( pc == c ) continue;
            System.out.println(pc.getSimpleName());
            entityMarshaller.setAdapter(pc, new FkXmlAdapter(pKeyField, c));
        }
    }

    void validateRestraintsFor(Class forcls, Object o)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class DefaultProperties extends Properties
    {


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

        File storeLoc = chcekStorePresence(contextMap.keySet(), stloc);
        if (storeLoc != null)
        {
            this.store = storeLoc;
        } else
        {
            modelMapping = prepareModelMapping(ContextReflectUtils.listAllSetFieldsFor(this.getClass()));
            mappedClasses = ContextReflectUtils.fieldsToContainedTypes(new LinkedList<Field>(modelMapping.keySet()));

            chceckAndCreateDictionaryStructure(modelMapping);

            refAdaptersInitializators.get(this.getClass()).initializeClass(this);

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
                    f.set(this, inst);
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
**/