package com.finebi.base.data.xml.imp;


import com.finebi.base.data.xml.XmlReader;
import com.finebi.base.data.xml.imp.handler.DefaultXmlReaderHandler;
import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;

/**
 * Created by andrew_asa on 2017/10/3.
 */
public class DefaultXmlReader extends DefaultHandler implements XmlReader {

    public DefaultXmlReader() {

    }

    public <T> T readXml(File src, Class clazz) throws Exception {

        XmlNode node = XmlNodeUtils.getXmlNodeByClass(clazz);
        return createObjectFromFile(clazz,node,src);
    }

    public <T> T createObjectFromFile(Class clazz, XmlNode node, File src) throws Exception {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        DefaultXmlReaderHandler handler = new DefaultXmlReaderHandler(node);
        parser.parse(src, handler);
        return (T)handler.getRoot();
    }

}
