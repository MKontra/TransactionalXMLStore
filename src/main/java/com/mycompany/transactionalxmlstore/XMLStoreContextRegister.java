/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.ContextReflectUtils;
import com.mycompany.transactionalxmlstore.utils.EntityReflectUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class XMLStoreContextRegister
{
    private Map<File, XMLStoreContextEntry> contextMap = new HashMap<File, XMLStoreContextEntry>();
    private XADiskOperationsProviderFactory provFact = new XADiskOperationsProviderFactory();
    
    public XMLStoreContextEntry registerContextForLocation(File f, Class c)
    {
        synchronized(this)
        {
            if ( contextMap.containsKey(f))
                return contextMap.get(f);
            
            XMLStoreContextEntry retval = new XMLStoreContextEntry();
            retval.contextDerivedClass = c;
            retval.managedSets = new HashMap<File, XMLStoreSetEntry>();
            List<Field> listAllSetFieldsFor = ContextReflectUtils.listAllSetFieldsFor(c);
            List<Class> modelClasses = ContextReflectUtils.fieldsToContainedTypes(listAllSetFieldsFor);
            for ( Field fld : listAllSetFieldsFor )
            {
                XMLStoreSetEntry xsse = new XMLStoreSetEntry();
                xsse.declaredField = fld;
                xsse.forClass = ContextReflectUtils.getXMLStoreSetContainedType(fld);
                xsse.withPKeyField = EntityReflectUtils.getPKeyField(xsse.forClass);
                
                File location = new File(f, fld.getName());
                location.mkdirs();
                
                xsse.provider = provFact.createProvider(modelClasses, location, xsse.forClass);
                
                retval.managedSets.put(location, xsse);
            }
            contextMap.put(f, retval);
            return retval;
        }
    }
}
