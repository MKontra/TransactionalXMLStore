/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore.utils.classvisitor;

import java.lang.reflect.Field;

/**
 *
 * @author Administrator
 */
public interface IFieldAction
{
    public void processField(Field f);
}
