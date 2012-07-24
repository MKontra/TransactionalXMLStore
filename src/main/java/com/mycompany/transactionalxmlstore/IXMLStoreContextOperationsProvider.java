/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import java.io.File;
import java.util.List;
import javax.transaction.Transaction;
import org.xadisk.bridge.proxies.interfaces.Session;

/**
 *
 * @author Administrator
 */
public interface IXMLStoreContextOperationsProvider
{
    public void add(Object o);
    public Object load(Object pkey);
    public List loadByAttribute(String name, Object key);
    public void remove(Object pkey);
    
    public boolean bindToSession(Session s);
    public boolean unbindSession();
}
