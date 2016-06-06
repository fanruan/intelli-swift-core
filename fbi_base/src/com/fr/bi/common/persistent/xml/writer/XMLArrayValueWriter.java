package com.fr.bi.common.persistent.xml.writer;

import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.stable.xml.XMLPrintWriter;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Connery on 2015/12/31.
 */
public class XMLArrayValueWriter extends XMLValueWriter {
    public XMLArrayValueWriter(BIBeanXMLWriterWrapper beanWrapper, Map<String, ArrayList<BIBeanXMLWriterWrapper>> disposedBeans) {
        super(beanWrapper, disposedBeans);
    }


    @Override
    void writeContent(XMLPrintWriter writer) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
        Object[] array = checkObjectArray(beanWrapper.getBean());
        if (array != null && array.length > 0) {
            if (!beanWrapper.getProperty()) {
                writer.startTAG("array");
            }
            stickInfo(writer);
            writeAppendInfo(writer, array.length);
            iteration(writer, array);
            if (!beanWrapper.getProperty()) {
                writer.end();
            }

        }
    }

    @Override
    protected void writeUUID(XMLPrintWriter writer) {
        /**
         * 跳掉父类的写UUID的
         */
    }

    protected void writeAppendInfo(XMLPrintWriter writer, int arraySize) {
        /**
         * 是否已经写过了，主要获取的是UUID，表面是统一个对象
         * 在方向生成对象时候需要，
         */
        String disposedUUID = getDisposedUUID(beanWrapper.getBean());
        if (disposedUUID == null) {
            disposedUUID = java.util.UUID.randomUUID().toString();
        }

        String append = combine(disposedUUID, arraySize);
        writer.attr(BIXMLTag.APPEND_INFO, append);
    }

    private String combine(String uuid, Integer arraySize) {
        return uuid + "#" + arraySize;
    }

    private void iteration(XMLPrintWriter writer, Object[] array) {
        for (int i = 0; i < array.length; i++) {
            try {
                if (array[i] != null) {
                    writer.startTAG("item");
                    BIBeanXMLWriterWrapper wrapper = new BIBeanXMLWriterWrapper(array[i]);
                    wrapper.setProperty(false);
                    if (isSampleObject(array[i])) {
                        writer.attr("sample", "yes");
                    } else {
                        writer.attr("sample", "no");
                    }
                    writer.attr("index", i);
                    wrapper.generateWriter(disposedBeans).writeValue(writer);
                    writer.end();
                }
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
                continue;
            }
        }
    }

    /**
     * @param object
     * @return
     */
    private Object[] checkObjectArray(Object object) {
        if (object != null) {
            if (object.getClass().isArray()) {
                return BITypeUtils.primitiveArray2BoxArray(object);
            }
        }
        return new Object[]{};

    }
}