package com.finebi.base.data.xml.process.impl;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.environmen.Environment;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.sax.TransformerHandler;
import java.util.Stack;

/**
 * @author andrew.asa
 * @create 2017-10-21
 **/
public class DefaultXmlNodeProcess extends AbstractXmlNodeProcess {

    @Override
    public void createXmlNodeByClass(Class clazz, XmlNode root) throws Exception {

    }

    @Override
    public boolean shouldDealWith(XmlNode node) {

        return false;
    }

    @Override
    public String getName() {

        return "DefaultXmlNodeProcess";
    }

    @Override
    public Object startElement(String uri, String localName, String qName, Attributes attributes,XmlNode node,Stack stack) {

        return null;
    }

    @Override
    public void characters(char[] ch, int start, int length, XmlNode node, Stack stack) {

    }

    @Override
    public Object endElement(Environment environment) {
        return null;
    }

    @Override
    public void afterEndElement(Environment environment) {

    }

    @Override
    public void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) {

    }
}
