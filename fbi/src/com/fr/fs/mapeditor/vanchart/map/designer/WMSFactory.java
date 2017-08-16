package com.fr.fs.mapeditor.vanchart.map.designer;

import com.fr.base.FRContext;
import com.fr.fs.mapeditor.vanchart.map.layer.WMSLayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WMSFactory {
    public static List<WMSLayer> readLayers(String xmlTxt){
        List<WMSLayer> layers = new ArrayList<WMSLayer>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlTxt));
            Document doc = builder.parse(is);

            Element root = doc.getDocumentElement();
            NodeList childNodes = root.getChildNodes();
            int length = childNodes.getLength();

            for (int i = 0; i < length; i++) {
                Node child = childNodes.item(i);
                if(child.getNodeName().equals("Capability")){
                    readCapability(child, layers);
                }
            }
        } catch (Exception e1){
            FRContext.getLogger().error(e1.getMessage(), e1);
        }
        return layers;
    }

    private static void readCapability(Node capability, java.util.List<WMSLayer> layers) {
        NodeList childNodes = capability.getChildNodes();
        int length = childNodes.getLength();

        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            if(child.getNodeName().equals("Layer")){
                readLayer(child, layers);
            }
        }
    }

    private static void readLayer(Node layer, java.util.List<WMSLayer> layers) {
        NodeList childNodes = layer.getChildNodes();
        int length = childNodes.getLength();

        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            if(child.getNodeName().equals("Layer")){
                readSingleLayer(child, layers);
            }
            readLayer(child, layers);//layer嵌套
        }
    }

    private static void readSingleLayer(Node layer, java.util.List<WMSLayer> layers){
        NodeList childNodes = layer.getChildNodes();
        int length = childNodes.getLength();
        WMSLayer wmsLayer = new WMSLayer();
        wmsLayer.setSelected(false);
        layers.add(wmsLayer);

        for (int i = 0; i < length; i++) {
            Node child = childNodes.item(i);
            if(child.getNodeName().equals("Name")){
                Node node = child.getFirstChild();
                wmsLayer.setLayer(node.getNodeValue());
            }
        }
    }
}
