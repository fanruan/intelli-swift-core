package com.fr.bi.common.persistent.xml.reader;


import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.common.persistent.xml.SimpleEntry;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLMapValueReader extends XMLValueReader {
    public XMLMapValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        try {
            String fieldClass = xmLableReader.getAttrAsString("class", "null");
            String uuid = xmLableReader.getAttrAsString(BIXMLTag.APPEND_INFO, "null");
            Map map = (Map) beanWrapper.getBean();
            if (!ComparatorUtils.equals(fieldClass, "null")) {
                BIBeanXMLReaderWrapper wrapper = getObjectWrapper(uuid, fieldClass);
                wrapper.generateReader(generatedBean).readValue(xmLableReader);
                if (wrapper.getBean() != null) {
                    SimpleEntry entry = (SimpleEntry) wrapper.getBean();
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}