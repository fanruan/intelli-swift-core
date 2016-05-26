package com.finebi.cube.conf.pack;

import com.fr.bi.common.persistent.xml.reader.BIBeanXMLReaderWrapper;
import com.fr.bi.common.persistent.xml.reader.XMLPersistentReader;
import com.fr.bi.common.persistent.xml.writer.BIBeanXMLWriterWrapper;
import com.fr.bi.common.persistent.xml.writer.XMLPersistentWriter;
import com.fr.file.XMLFileManager;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;

/**
 * Created by Connery on 2016/1/4.
 */
public class XMLConfigureGenerator extends XMLFileManager {
    private String fileName;
    private Object target;
    private String tagName;

    public XMLConfigureGenerator(String fileName, Object target, String tagName) {
        this.fileName = fileName;
        this.target = target;
        this.tagName = tagName;
    }

    @Override
    public String fileName() {
        return fileName;
    }

    @Override
    public void readXML(XMLableReader xmLableReader) {
        XMLPersistentReader reader = new XMLPersistentReader(new HashMap<String, BIBeanXMLReaderWrapper>(), new BIBeanXMLReaderWrapper(target));
        xmLableReader.readXMLObject(reader);
    }

    @Override
    public void writeXML(XMLPrintWriter xmlPrintWriter) {
        BIBeanXMLWriterWrapper wrapper = new BIBeanXMLWriterWrapper(target);
        wrapper.setTag(tagName);
        wrapper.setTagAvailable(true);
        wrapper.setProperty(false);
        XMLPersistentWriter object = new XMLPersistentWriter(wrapper);
        object.writeXML(xmlPrintWriter);
    }
}