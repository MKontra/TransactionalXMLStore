/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.FileToIFileOperations;
import com.mycompany.transactionalxmlstore.utils.IFileOperations;
import com.mycompany.transactionalxmlstore.utils.ModelInfo;
import com.mycompany.transactionalxmlstore.utils.XADiskSessionToIFileOperations;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transaction;
import org.xadisk.bridge.proxies.interfaces.Session;

/**
 *
 * @author Administrator
 */
public class XMLStoreContextOperationsProviderImpl implements IXMLStoreContextOperationsProvider
{

    private Class modelClass;
    private ModelInfo modelInfo;
    private List<Class> modelClasses;
    private File location;
    private Session trans;
    private final IFileOperations FileAdapterInstance = new FileToIFileOperations();
    private XStream xstream = new XStream();

    private class ModelState
    {
        boolean dirty;
        Object oldCopy;
    }
    private Map<File, ModelState> trackingState = new HashMap<File, ModelState>();

    public XMLStoreContextOperationsProviderImpl(List<Class> modelClasses, File location, Class modelClass)
    {
        this.modelClass = modelClass;
        this.modelClasses = modelClasses;
        this.location = location;
        this.modelInfo = ModelInfo.getModelInfoFor(modelClass);
        this.xstream.registerConverter(XMLStoreConverter.getEntityConverter(modelClasses, modelInfo));
    }

    @Override
    public void add(Object o)
    {
        IFileOperations ops = trans == null ? FileAdapterInstance : new XADiskSessionToIFileOperations(trans);
        try
        {
            File toAdd = new File(location + File.separator + modelInfo.getpKeyField().get(o));
            ops.createFile(toAdd, false);
            xstream.toXML(o, ops.createFileOutputStream(toAdd));
        } catch (Exception ex)
        {
            Logger.getLogger(XMLStoreContextOperationsProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public Object load(Object pkey)
    {
        IFileOperations ops = trans == null ? FileAdapterInstance : new XADiskSessionToIFileOperations(trans);
        try
        {
            File toLoad = new File(location + File.separator + pkey);
            return xstream.fromXML(ops.createFileInputStream(toLoad, true));
        } catch (Exception ex)
        {
            Logger.getLogger(XMLStoreContextOperationsProviderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public List loadByAttribute(String name, Object key)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove(Object pkey)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean bindToSession(Session s)
    {
        if (trans == null)
        {
            trans = s;
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean unbindSession()
    {
        if (trans == null)
        {
            return false;
        } else
        {
            trans = null;
            return false;
        }
    }
}
