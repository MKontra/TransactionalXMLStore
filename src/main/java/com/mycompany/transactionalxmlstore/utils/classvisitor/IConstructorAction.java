/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils.classvisitor;

import java.lang.reflect.Constructor;

/**
 *
 * @author Administrator
 */
public interface IConstructorAction
{
    public void processConstructor(Constructor c);
}
