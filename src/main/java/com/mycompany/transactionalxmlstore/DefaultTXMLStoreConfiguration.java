/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

/**
 *
 * @author Administrator
 */
class DefaultTXMLStoreConfiguration extends TXMLStoreConfiguration{

    public DefaultTXMLStoreConfiguration() {
        StackTraceElement[] st = new Throwable().getStackTrace();
        this.location = st[0].getClassName() + "Storage";
    }

    DefaultTXMLStoreConfiguration(String location) {
        this.location = location;
    }
    
}
