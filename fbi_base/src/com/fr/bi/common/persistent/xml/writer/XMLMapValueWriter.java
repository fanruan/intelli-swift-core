package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.common.persistent.xml.SimpleEntry;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLMapValueWriter extends XMLCollectionValueWriter {
    public XMLMapValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }

    public static String MAP_TAG = "BI_XML_MAP_TAG";

    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        Map map = (Map) beanWrapper.getBean();
        /**
         * Map转换成数组处理
         */
        writeIteration(writer, convert(map), MAP_TAG);
    }

    private ArrayList<SimpleEntry> convert(Map map) {
        ArrayList<SimpleEntry> result = new ArrayList<SimpleEntry>();
        if (map != null) {
            Iterator<Map.Entry> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry element = it.next();
                result.add(new SimpleEntry(element.getKey(), element.getValue()));
            }
        }
        return result;
    }


}