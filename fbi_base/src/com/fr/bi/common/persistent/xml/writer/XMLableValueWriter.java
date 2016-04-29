package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 * 添加支持自定义XML的解析，目的是兼容现有一些对象。
 * 以后这个一定要去掉。主要原因是XML自定义开放后，自动解析和自定义解析
 * 容易相互调用导致循环，使用上一定要小心。
 * TODO 不在支持自定义的XML
 * Created by Connery on 2015/12/31.
 */
@Deprecated
public class XMLableValueWriter extends XMLValueWriter {
    public XMLableValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }


    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        XMLWriter xmlWriter = (XMLWriter) beanWrapper.getBean();
        xmlWriter.writeXML(writer);
    }

    protected static Boolean isXMLWritableValue(Class clazz) {
        return BITypeUtils.isAssignable(XMLValueWriter.class, clazz);
    }
}