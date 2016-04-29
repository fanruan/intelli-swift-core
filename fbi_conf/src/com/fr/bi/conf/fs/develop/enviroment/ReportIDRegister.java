package com.fr.bi.conf.fs.develop.enviroment;


import com.fr.bi.conf.fs.develop.DeveloperConfig;
import com.fr.stable.StableUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Connery on 2015/1/3.
 */
public class ReportIDRegister {
    private static final String CONFIG_NAME = "report_id.xml";
    private Document document;
    private Element root;
    private String basicPath;
    private ArrayList<String> reportIDList;

    public ReportIDRegister(String path) {
        basicPath = path;
        File currentFile = new File(basicPath);
        if (!currentFile.exists()) {
            StableUtils.mkdirs(currentFile);
            reportIDList = new ArrayList<String>();
            writeIDs(basicPath + CONFIG_NAME, reportIDList);
        } else {
            reportIDList = parseID(basicPath + CONFIG_NAME);
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.newDocument();
            root = this.document.createElement("report");
            this.document.appendChild(root);
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }

    public ReportIDRegister() {
        this(DeveloperConfig.getTestBasicPath() + DeveloperConfig.FILESEPARATOR + "config" + DeveloperConfig.FILESEPARATOR);
    }

    public static void writeXML(String fileName, Document document) {
        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "gb2312");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
            System.out.println(fileName + " 文件成功!");
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addID(String id) {
        if (!reportIDList.contains(id)) {
            reportIDList.add(id);
            writeIDs();
        }
    }

    private void writeIDs() {
        writeIDs(basicPath + CONFIG_NAME, reportIDList);
    }

    private void writeIDs(String fileName, ArrayList<String> ids) {

        Iterator<String> it = ids.iterator();
        while (it.hasNext()) {
            String id = it.next();
            Element idEle = this.document.createElement("ID");
            idEle.setTextContent(id);
            root.appendChild(idEle);
        }
        writeXML(fileName, document);
    }

    private ArrayList<String> parseID(String idFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        ArrayList<String> ids = new ArrayList<String>();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(idFile);
            NodeList nodelist = document.getElementsByTagName("ID");
            for (int i = 0; i < nodelist.getLength(); i++) {
                ids.add(nodelist.item(i).getTextContent());
            }
        } catch (Exception ex) {

        }
        return ids;
    }

    public ArrayList<String> getReportIDList() {
        return reportIDList;
    }
}