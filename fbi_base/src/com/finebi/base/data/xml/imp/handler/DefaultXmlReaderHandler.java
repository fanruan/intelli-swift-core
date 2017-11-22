package com.finebi.base.data.xml.imp.handler;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.environmen.Environment;
import com.finebi.base.utils.data.list.ListUtils;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by andrew_asa on 2017/10/9.
 * 读处理器
 */
public class DefaultXmlReaderHandler extends DefaultHandler {

    XmlNode node = null;

    List<String> chain = new ArrayList<String>();

    Object root;

    Stack stack = new Stack();

    public DefaultXmlReaderHandler(XmlNode node) {

        this.node = node;
    }


    @Override
    public void startDocument() {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        try {
            if (ListUtils.isEmptyList(chain)) {
                chain.add(qName);
                root = node.getClazz().newInstance();
                stack.push(root);
                return;
            }
            chain.add(qName);
            XmlNode n = getCurrentNode();
            XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(n);
            process.startElement(uri, localName, qName, attributes, n, stack);
        } catch (Exception e) {

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        XmlNode node = getCurrentNode();
        XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(node);
        Environment environment = Environment.createEnvironment(uri, localName, qName, node, stack, chain);
        Object last = process.endElement(environment);
        ListUtils.removeLastElement(chain);
        XmlNode pnode = getCurrentNode();
        process = XmlStructureNodeFactory.getProcessByXmlNode(pnode);
        if (last != null) {
            environment = Environment.createEnvironment(stack, qName, last);
        } else {
            environment = Environment.createEnvironment();
        }
        process.afterEndElement(environment);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        XmlNode n = getCurrentNode();
        XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(n);
        process.characters(ch, start, length, n, stack);
    }

    public Object getRoot() {

        return root;
    }

    private XmlNode getCurrentNode() {

        return XmlNodeUtils.getNodeByChain(node, chain);
    }
}
