package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.program.BIFieldUtils;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public abstract class XMLValueWriter {
    protected BIBeanXMLWriterWrapper beanWrapper;
    protected Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans;

    public XMLValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        this.beanWrapper = beanWrapper;
        this.disposedBeans = disposedBeans;
    }


    public void writeValue(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        Object value = beanWrapper.getBean();
        if (value != null) {
            /**
             * 处理相同的引用
             */
            String disposedUUID = getDisposedUUID(value);
            if (disposedUUID != null) {
                beanWrapper.setSampleModel(true);
                beanWrapper.setUUID(disposedUUID);
            } else {
                setDisposed(beanWrapper);
            }
            if (!beanWrapper.getProperty() && beanWrapper.getTagAvailable()) {
                /**
                 * 如果不是属性对象，一定要有Tag
                 */

                writer.startTAG(beanWrapper.getTag());

            }

            writeContent(writer);
            if (!beanWrapper.getProperty() && beanWrapper.getTagAvailable()) {

                writer.end();
            }
        }
    }

    abstract void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException;


    protected String getDisposedUUID(Object obj) {
        if (this instanceof XMLBasicValueWriter) {
            return null;
        }
        if (disposedBeans.containsKey(obj.getClass().getName())) {
            Iterator<BIBeanXMLWriterWrapper> iterator = disposedBeans.get(obj.getClass().getName()).iterator();
            while (iterator.hasNext()) {
                BIBeanXMLWriterWrapper wrapper = iterator.next();
                if (wrapper.getBean() == obj) {
                    return wrapper.getUUID();
                }
            }
        }
        return null;
    }

    private void setDisposed(BIBeanXMLWriterWrapper wrapper) {
        if (this instanceof XMLBasicValueWriter) {
            return;
        }
        if (!disposedBeans.containsKey(wrapper.getBean().getClass().getName())) {
            disposedBeans.put(wrapper.getBean().getClass().getName(), new ArrayList<BIBeanXMLWriterWrapper>());
        }
        disposedBeans.get(wrapper.getBean().getClass().getName()).add(wrapper);

    }


    protected void stickInfo(XMLPrintWriter writer) {
        writeUUID(writer);
        writer.attr("class", beanWrapper.getBean().getClass().getName());
    }

    protected void writeUUID(XMLPrintWriter writer) {
        /**
         * 是否已经写过了，主要获取的是UUID，表面是统一个对象
         * 在方向生成对象时候需要，
         */
        String disposedUUID = getDisposedUUID(beanWrapper.getBean());
        if (disposedUUID == null) {
            disposedUUID = java.util.UUID.randomUUID().toString();
        }
        writer.attr(BIXMLTag.APPEND_INFO, disposedUUID);
    }

    /**
     * 有些情况需要使用字段的类型作为
     *
     * @return
     */
//    protected boolean useFieldType() {
//        if (beanWrapper.getField() == null) {
//            return false;
//        }
//        if (ComparatorUtils.equals(beanWrapper.getBean().getClass(), Collections.unmodifiableMap(new HashMap<Object, Object>()).getClass())) {
//            return true;
//        }
//    }
    protected static Boolean isSampleObject(Object object) {
        return BIFieldUtils.isBasicType(object.getClass());
    }
}