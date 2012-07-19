/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils.classvisitor;

import java.lang.reflect.Method;

/**
 *
 * @author Administrator
 */
public interface IMethodAction
{
    public void processMethod(Method m);
}
