/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.XMLStoreContextTestSuite.TestModel;

import javax.persistence.Id;

/**
 *
 * @author Administrator
 */
public class TestObject1 {
    @Id
    public long Id;
    
    public String Description;
    
    @javax.persistence.OneToMany
    public TestObject2 to2ref;
    
}
