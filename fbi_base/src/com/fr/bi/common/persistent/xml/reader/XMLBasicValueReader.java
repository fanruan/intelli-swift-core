package com.fr.bi.common.persistent.xml.reader;


import com.fr.bi.common.persistent.xml.writer.XMLBasicValueWriter;
import com.fr.bi.stable.utils.program.BITypeUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLBasicValueReader extends XMLValueReader {
    public XMLBasicValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        if (xmLableReader.isChildNode()) {
            if (ComparatorUtils.equals(xmLableReader.getTagName(), XMLBasicValueWriter.BASIC_TAG)) {
                String str = xmLableReader.getAttrAsString("value", "null");
                Object value = BITypeUtils.stringConvert2BasicType(beanWrapper.getBeanClass(), str);
                beanWrapper.setBean(value);
            }
        }
    }
}