package com.fr.bi.common.persistent.xml.reader;

import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;

import java.util.Map;

/**
 * Created by Connery on 2016/1/2.
 */
public class XMLableValueReader extends XMLValueReader {
    public XMLableValueReader(BIBeanXMLReaderWrapper beanWrapper, Map<String, BIBeanXMLReaderWrapper> generatedBean) {
        super(beanWrapper, generatedBean);
    }

    @Override
    protected void readerContent(XMLableReader xmLableReader) {
        ((XMLReadable) beanWrapper.getBean()).readXML(xmLableReader);
    }
}