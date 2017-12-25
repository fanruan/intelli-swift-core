package com.finebi.base.data.xml.process;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.environmen.Environment;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Stack;

public interface XmlNodeProcess {

    void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception;

    boolean shouldDealWith(XmlNode node);

    String getName();

    Object startElement(String uri, String localName, String qName, Attributes attributes, XmlNode node, Stack stack) throws Exception;

    void characters(char[] ch, int start, int length, XmlNode node, Stack stack);

    Object endElement(Environment environment);

    void afterEndElement(Environment environment);

    void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr)throws Exception;
}
