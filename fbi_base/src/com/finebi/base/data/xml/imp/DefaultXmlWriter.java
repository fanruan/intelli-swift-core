package com.finebi.base.data.xml.imp;


import com.finebi.base.data.xml.node.XmlNode;
import com.finebi.base.data.xml.process.XmlNodeProcess;
import com.finebi.base.data.xml.process.XmlStructureNodeFactory;
import com.finebi.base.utils.data.xml.imp.node.XmlNodeUtils;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Created by andrew_asa on 2017/9/30.
 */
public class DefaultXmlWriter {

    public DefaultXmlWriter() {

    }

    public void write(File dst, Object obj) throws Exception {

        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler = factory.newTransformerHandler();
        Transformer transformer = handler.getTransformer();
        // 是否自动添加额外的空白
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        // 设置字符编码
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        StreamResult result = new StreamResult(dst);
        handler.setResult(result);
        // 开始xml
        handler.startDocument();
        AttributesImpl attributes = new AttributesImpl();
        XmlNode node = XmlNodeUtils.getXmlNodeByClass(obj.getClass());
        writeNode(obj, node, handler, attributes);
        handler.endDocument();
    }

    private void writeNode(Object o, XmlNode node, TransformerHandler handler, AttributesImpl attr) throws Exception {

        XmlNodeProcess process = XmlStructureNodeFactory.getProcessByXmlNode(node);
        process.writeNode(o, node, handler, attr);
    }
}
