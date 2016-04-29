package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLIterableValueWriter extends XMLValueWriter {
    public XMLIterableValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }

    public static String ITERATION_MAP = "BI_XML_ITERATION_TAG";

    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        Iterable array = (Iterable) beanWrapper.getBean();
        writeIteration(writer, array, ITERATION_MAP);

    }

    /**
     * 写循环内部对象
     *
     * @param writer
     * @param array
     * @param vacancyTag 如果当前数组对象不是一个属性的话，例如ArrayList<ArrayList<String>>中的
     *                   ArrayList<String>，当前对象是没有属性名作为标签的，所有需要添加默认标签
     */
    protected void writeIteration(XMLPrintWriter writer, Iterable array, String vacancyTag) {
        /**
         * 迭代对象需要自己解析。
         */
        if (array != null) {
//            if (!beanWrapper.getProperty()) {
//                writer.startTAG(vacancyTag);
//            }
            stickInfo( writer);
            /**
             * 解构Array
             */
            try {
                Iteration(writer, array);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
//            if (!beanWrapper.getProperty()) {
//                writer.end();
//            }
        }
    }


    public void Iteration(XMLPrintWriter writer, Iterable array) {
        Iterator it = array.iterator();

        while (it.hasNext()) {
            Object element = it.next();
            try {
                writer.startTAG("item");
                BIBeanXMLWriterWrapper wrapper = new BIBeanXMLWriterWrapper(element);
                wrapper.setProperty(false);
                /**
                 * 已经生成设置过了  writer.startTAG("iterable");
                 */
                wrapper.setTagAvailable(false);
                if (isSampleObject(element)) {
                    writer.attr("sample", "yes");
                } else {
                    writer.attr("sample", "no");
                }
                wrapper.generateWriter(disposedBeans).writeValue(writer);
                writer.end();

            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                writer.end();
                continue;
            }
        }
    }
}