/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.transactionalxmlstore;

import com.mycompany.transactionalxmlstore.utils.ModelInfo;
import com.mycompany.transactionalxmlstore.utils.PrimaryKeyUtils;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.AbstractBasicConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class XMLStoreConverter implements Converter
{

    private static Map<Class, Converter> entityConvertors = new HashMap<Class, Converter>();
    private static Map<Class, Converter> entityReferenceConvertors = new HashMap<Class, Converter>();
    private boolean normal;
    private ModelInfo mInfo;
    private List<Class> treatAsReferences;

    private XMLStoreConverter(ModelInfo mInfo, List<Class> treatAsReferences, boolean normal)
    {
        this.normal = normal;
        this.mInfo = mInfo;
        this.treatAsReferences = treatAsReferences;
    }

    public static Converter getEntityConverter(List<Class> model, ModelInfo minfo)
    {
        Class normalModelClass = minfo.getForClass();
        Converter normalCnv = entityConvertors.get(normalModelClass);
        if (normalCnv == null)
        {
            normalCnv = new XMLStoreConverter(minfo, model, true);
            entityConvertors.put(normalModelClass, normalCnv);
        }


        for (Class refC : model)
        {
            Converter refConv = entityReferenceConvertors.get(refC);
            if (refConv == null)
            {
                refConv = new XMLStoreConverter(ModelInfo.getModelInfoFor(refC), null, false);
            }
            entityReferenceConvertors.put(refC, refConv);
        }
        return normalCnv;

    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc)
    {
        if (normal == true)
        {
            for (Field f : mInfo.getAllFields())
            {
                writer.startNode(f.getName());
                if (treatAsReferences.contains(f.getType()))
                {
                    try
                    {
                        mc.convertAnother(f.get(o), entityReferenceConvertors.get(f.getType()));
                    } catch (Exception ex)
                    {
                        Logger.getLogger(XMLStoreConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else
                {
                    try
                    {
                        mc.convertAnother(f.get(o));
                    } catch (Exception ex)
                    {
                        Logger.getLogger(XMLStoreConverter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                writer.endNode();
            }
        } else
        {
            Object pKeyValue = PrimaryKeyUtils.getPrimaryKeyValue(mInfo.getpKeyField(), o);
            //writer.startNode(mInfo.getForClass().getCanonicalName());
            writer.startNode("fKey");
            mc.convertAnother(pKeyValue);
            writer.endNode();
            //writer.endNode();
        }
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc)
    {

        Object unm = mInfo.getClassInstance();
        while (reader.hasMoreChildren())
        {
            try
            {
                reader.moveDown();
                String fieldName = reader.getNodeName();
                Field f = mInfo.getFieldForName(fieldName);
                if (!treatAsReferences.contains(f.getType()))
                {
                    Object fieldValue = uc.convertAnother(unm, f.getType());
                    f.set(unm, fieldValue);
                } else
                {
                    ModelInfo fKmI = ModelInfo.getModelInfoFor(f.getType());
                    Object newProxy = ModelInfo.getProxyFor(f.getType());

                    reader.moveDown();
                    Object pKeyVal = uc.convertAnother(newProxy, fKmI.getpKeyType());
                    reader.moveUp();
                    fKmI.getFieldForName("Id").set(newProxy, pKeyVal);
                    f.set(unm, newProxy);
                }
                reader.moveUp();
            } catch (Exception ex)
            {
                Logger.getLogger(XMLStoreConverter.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error unmarshalling XML stream", ex);
            }
        }
        return unm;
    }

    @Override
    public boolean canConvert(Class type)
    {
        return type == mInfo.getForClass();
    }
}
