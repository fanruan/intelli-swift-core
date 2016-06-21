package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.common.persistent.xml.BIIgnoreField;
import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLNormalValueWriter extends XMLValueWriter {

    public static boolean IS_IGNORED_FIELD_USABLE = true;

    @Override
    protected String getDisposedUUID(Object obj) {
        return super.getDisposedUUID(obj);
    }

    public XMLNormalValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }


    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        writer.attr("class", beanWrapper.getBean().getClass().getName());
        if (beanWrapper.getUUID() != null) {
            writer.attr(BIXMLTag.APPEND_INFO, beanWrapper.getUUID());
        }
        writeFieldValue(writer);
    }

    private List<Field> fetchAllObjectFields() {
        return beanWrapper.getAllObjectFields();
    }

    private List<Field> fetchPropertyDescriptorFields() {
        return beanWrapper.getPropertyDescriptorFields();
    }

    private void writeFieldValue(XMLPrintWriter writer) {
        Iterator<Field> it = fetchAllObjectFields().iterator();
        while (it.hasNext()) {
            try {
                Field field = it.next();
                if (needDispose(field)) {
                    Object fieldValue = beanWrapper.getOriginalValue(field);
                    if (fieldValue != null) {
                        BIBeanXMLWriterWrapper wrapper = new BIBeanXMLWriterWrapper(fieldValue);
                        fieldTagStart(field, writer);
//                    writer.attr("class", fieldValue.getClass().getName());
                        /**
                         * 这里都是属性beanWrapper
                         */
                        wrapper.setProperty(true);
                        wrapper.setPropertyField(field);
                        if (beanWrapper.getSampleModel()) {
                            if (wrapper.isBasicBean()) {
                                wrapper.generateWriter(disposedBeans).writeValue(writer);
                            }
                        } else {
                            wrapper.generateWriter(disposedBeans).writeValue(writer);
                        }
                        fieldTagEnd(writer);
                    }
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                fieldTagEnd(writer);
                continue;
            }
        }
    }

    private boolean needDispose(Field field) {
        return !isIgnore(field);
    }

    private boolean isIgnore(Field field) {
        return IS_IGNORED_FIELD_USABLE && field.isAnnotationPresent(BIIgnoreField.class);
    }

    private void fieldTagStart(Field field, XMLPrintWriter writer) {
        writer.startTAG(BIXMLTag.FIELD_INFO);
        writer.attr(BIXMLTag.FIELD_NAME, field.getName());
    }

    private void fieldTagEnd(XMLPrintWriter writer) {
        writer.end();
    }

}