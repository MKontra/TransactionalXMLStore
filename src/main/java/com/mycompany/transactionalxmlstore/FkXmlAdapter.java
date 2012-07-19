/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.PrimaryKeyUtils;
import java.lang.reflect.Field;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Administrator
 */
public class FkXmlAdapter extends XmlAdapter
{
    private final Field pKField;

    public FkXmlAdapter(Field pKField, Class c)
    {
        this.pKField = pKField;
    }

    @Override
    public Object unmarshal(Object v) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object marshal(Object v) throws Exception
    {
        System.out.println( "Marshalling" );
        return PrimaryKeyUtils.getPrimaryKeyValue(pKField, v);
    }
    
}
