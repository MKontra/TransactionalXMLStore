/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils.classvisitor;

/**
 *
 * @author Administrator
 */
public interface IScopeManagement
{
    public void enteringDeclarationScope(Class c);
    public void exitingDeclarationScope(Class c);
}
