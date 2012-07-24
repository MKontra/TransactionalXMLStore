/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.XMLStoreContextTestSuite;

import junit.framework.TestCase;
import com.mycompany.transactionalxmlstore.*;
import com.mycompany.transactionalxmlstore.XMLStoreContextTestSuite.TestModel.TestObject1;
import com.mycompany.transactionalxmlstore.XMLStoreContextTestSuite.TestModel.TestObject2;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class XMLStoreContextTest extends TestCase {
    
    public XMLStoreContextTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private static class TestContext extends XMLStoreContext
    {
        public XMLStoreSet<TestObject1> to1;
        public XMLStoreSet<TestObject2> to2;
    }
    
    public void testSetExtraction() {
        TestContext testModelCntx = new TestContext();
        try
        {
            TestObject1 to1i = new TestObject1();
            to1i.Id = 1;
            to1i.Description = "Simple Object";
            
            TestObject2 to2i = new TestObject2();
            to2i.Id = 1;
            to2i.Description = "Simple object different class";
            
            to1i.to2ref = to2i;
            testModelCntx.to1.add(to1i);
        } catch (Exception ex)
        {
            Logger.getLogger(XMLStoreContextTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try
        {
            TestObject1 toUnmc = testModelCntx.to1.load(1);
            System.out.println("co je totok za jebnuty debugger");
        } catch (Exception ex)
        {
            Logger.getLogger(XMLStoreContextTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
