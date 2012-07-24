/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.IFileOperations;
import java.io.File;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class XADiskOperationsProviderFactory
{
    public IXMLStoreContextOperationsProvider createProvider(List<Class> model, File location, Class modelClass)
    {
        return new XMLStoreContextOperationsProviderImpl(model, location, modelClass);
    }
}
