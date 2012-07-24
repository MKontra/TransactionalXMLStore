/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;
/**
 *
 * @author Administrator
 */
public class XMLStoreConfiguration
{

    private String location;

    public XMLStoreConfiguration setLocation(String s)
    {
        this.location = s;
        return this;
    }
    
    public String getLocation()
    {
        return location;
    }
}
