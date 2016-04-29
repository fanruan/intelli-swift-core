package com.fr.bi.common.persistent.xml.reader;

import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLNormalValueReader extends XMLValueReader {
    public XMLNormalValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        try {
            String fieldName = xmLableReader.getTagName();
            String fieldClass = xmLableReader.getAttrAsString("class", "null");
            String uuid = xmLableReader.getAttrAsString(BIXMLTag.APPEND_INFO, "null");
            BIBeanXMLReaderWrapper wrapper;
            Object fieldValue = beanWrapper.getOriginalValue(fieldName);
            if (fieldValue != null) {
                /**
                 * 由于使用了对象的强制构造，那么
                 * 如果属性在对象构造的时候可能被new了一个对象，那么这里fieldValue
                 * 不是null，因此不会经过反射构造，也因此该属性对象没有被注册到
                 * 已生成的集合中。需要使用getObjectWrapper，进行生成对象的注册
                 * */
                wrapper = getObjectWrapper(uuid, fieldValue);
//                new BIBeanXMLReaderWrapper(fieldValue);

            } else if (!ComparatorUtils.equals(fieldClass, "null")) {
                wrapper = getObjectWrapper(uuid, fieldClass);
            } else {
                return;
            }
            wrapper.generateReader(generatedBean).readValue(xmLableReader);
            if (wrapper.getBean() != null) {
                beanWrapper.setOriginalValue(fieldName, wrapper.getBean());
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }

    }
}