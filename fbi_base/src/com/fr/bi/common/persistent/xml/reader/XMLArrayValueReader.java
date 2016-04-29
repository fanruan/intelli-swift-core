package com.fr.bi.common.persistent.xml.reader;

import com.fr.bi.common.persistent.xml.BIXMLTag;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLArrayValueReader extends XMLValueReader {
    public XMLArrayValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        try {
            if (!beanWrapper.getDisposed()) {
                String fieldClass = xmLableReader.getAttrAsString("class", "null");
                int index = xmLableReader.getAttrAsInt("index", -1);
                if (index != -1) {
                    String uuid = xmLableReader.getAttrAsString(BIXMLTag.APPEND_INFO, "null");
                    beanWrapper.getBean();
                    if (!ComparatorUtils.equals(fieldClass, "null")) {
                        BIBeanXMLReaderWrapper wrapper = getObjectWrapper(uuid, fieldClass);
                        wrapper.generateReader(generatedBean).readValue(xmLableReader);
                        if (wrapper.getBean() != null) {
                            BITypeUtils.addArrayValue(beanWrapper.getBean(), index, wrapper.getBean());
                        }
                    }
                } else {
                    throw BINonValueUtils.beyondControl();
                }
            }
        } catch (Exception e) {
            BILogger.getLogger().error(e.getMessage(), e);
        }
    }
}