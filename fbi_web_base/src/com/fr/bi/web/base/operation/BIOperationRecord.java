package com.fr.bi.web.base.operation;

import com.fr.fs.base.entity.User;
import com.fr.fs.control.UserControl;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONObject;
import com.fr.stable.xml.*;
import com.fr.third.javax.xml.stream.events.Characters;
import com.fr.third.javax.xml.stream.events.StartElement;
import com.fr.third.javax.xml.stream.events.XMLEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by Connery on 14-11-11.
 */
public class BIOperationRecord {
    private static final String XML_ROOT = "record_report";
    private static final String XML_ROOT_ID = "record_id";
    private static final String XML_CREAT_TIME = "creattime";
    private static final String XML_REPORT_ID = "report_id";
    private static final String XML_OPERATIONS = "operations";
    private static final String XML_OPERATION_ITEM = "item";
    private static final String XML_OPERATION_TIME = "time";
    private static final String XML_OPERATION_CONTENT = "content";
    private static final String FILE_NAME_DIVIDER = "&&";
    private String reportName;
    private long userId;
    private long id;
    private ArrayList<BIOperationItem> items;
    private String filePath;
    private String createDate;

    /**
     * 构造函数
     *
     * @param userId     用户
     * @param reportId   报表
     * @param reportName 用户名
     * @throws Exception
     */
    public BIOperationRecord(long userId, long reportId, String reportName) throws Exception {
        items = new ArrayList<BIOperationItem>();
        this.id = reportId;
        this.reportName = reportName;
        this.userId = userId;
        BIUserOperationManager.checkFold(userId);
        initialRecordFromFile();
    }

    /**
     * 构造函数
     *
     * @param userId     用户
     * @param reportName 报表名
     * @throws Exception
     */
    public BIOperationRecord(long userId, String name) throws Exception {
        this.userId = userId;
        if (name.contains(FILE_NAME_DIVIDER)) {
            initialWithFileName(name);
        } else {
            initialWithReportName(name);
        }

    }


    /**
     * 构造函数
     *
     * @param userId 用户
     * @param id     报表id
     * @throws Exception
     */
    public BIOperationRecord(long userId, long id) throws Exception {
        this.userId = userId;
        this.id = id;
        initialWithReportId(id);
    }

    /**
     * 获得该模板创建时间
     *
     * @return
     */
    public String getCreateDate() {
        return createDate;
    }

    private void initialRecordFromFile() throws Exception {
        StringBuffer sb = BIUserOperationManager.getParentDirectoryPath(userId);
        sb.append(BIUserOperationManager.FILESEPARATOR).append(String.valueOf(this.id)).append(FILE_NAME_DIVIDER).append(reportName);
        filePath = sb.toString();
        File recordFile = new File(filePath);
        if (recordFile.exists()) {
            initialItem(this.readFile());
        }
    }

    /**
     * initial the records according the report name.
     * But if there exists duplicate report name under current fold,just use the file firstly meet when do retrieve
     *
     * @param reportName the report name used to retrieve the record
     * @throws Exception
     */
    private void initialWithReportName(String reportName) throws Exception {
        initialWithPartName(reportName);
    }

    private void initialWithReportId(long id) throws Exception {
        initialWithPartName(Long.toString(id));
    }

    private void initialWithPartName(String name) throws Exception {
        File[] recordFiles = BIUserOperationManager.fetchAllFiles(userId);
        for (int i = 0; i < recordFiles.length; i++) {
            if (recordFiles[i].getName().contains(name)) {
                initialWithFileName(recordFiles[i].getName());
                break;
            }
        }
    }


    private void initialWithFileName(String fileName) throws Exception {
        String[] id_name = getParaFromFileName(fileName);
        if (id_name != null) {
            items = new ArrayList<BIOperationItem>();
            this.id = Long.parseLong(id_name[0]);
            this.reportName = id_name[1];
            BIUserOperationManager.checkFold(userId);
            initialRecordFromFile();
        }
    }


    private String[] getParaFromFileName(String fileName) {
        if (fileName != null && !ComparatorUtils.equals(fileName, "")) {
            String[] id_name = fileName.split(FILE_NAME_DIVIDER);
            if (id_name.length == 2) {
                return id_name;
            }
        }
        return new String[0];
    }

    private void initialItem(ArrayList<BIOperationItem> items) {
        if (!items.isEmpty()) {
            this.items.addAll(items);
        }
    }

    /**
     * 保存一个操作到数组
     *
     * @param content 内容
     * @throws Exception
     */
    public void save(String content) throws Exception {
        BIOperationItem item = new BIOperationItem(content);
        if (this.items.size() > 0) {
            BIOperationItem lastOperation = this.items.get(this.items.size() - 1);
            if (!lastOperation.repeatOperation(item)) {
                items.add(item);
            }
        } else {
            items.add(item);
        }
        writeFile();
    }

    private ArrayList<BIOperationItem> readFile() throws Exception {
        ArrayList<BIOperationItem> items = new ArrayList<BIOperationItem>();
        java.io.Reader fileReader = new FileReader(filePath);
        XMLableReader reader = XMLableReader.createXMLableReader(fileReader);
        if (reader == null) {
            return items;
        }
        if (ComparatorUtils.equals(XML_ROOT, reader.getTagName())) {
            this.createDate = reader.getAttrAsString(XML_CREAT_TIME, "");
        }
        com.fr.third.javax.xml.stream.XMLEventReader eventReader = reader.getXMLEventReader();
        com.fr.third.javax.xml.stream.events.XMLEvent currentEvent;
        while (eventReader.hasNext()) {
            currentEvent = eventReader.nextEvent();
            if (currentEvent.isStartElement()) {
                StartElement startElement = currentEvent.asStartElement();
                com.fr.third.javax.xml.namespace.QName qName = startElement.getName();
                if (ComparatorUtils.equals(XML_OPERATIONS, qName.getLocalPart())) {
                    items = parseItemElement(eventReader);
                }
            }
        }
        return items;
    }

    private ArrayList<BIOperationItem> parseItemElement(com.fr.third.javax.xml.stream.XMLEventReader eventReader) throws Exception {
        ArrayList<BIOperationItem> items = new ArrayList<BIOperationItem>();
        com.fr.third.javax.xml.stream.events.XMLEvent currentEvent;
        String time = "";
        String content = "";
        while (eventReader.hasNext()) {
            currentEvent = eventReader.nextEvent();
            if (currentEvent.isStartElement()) {
                StartElement startElement = currentEvent.asStartElement();
                com.fr.third.javax.xml.namespace.QName qName = startElement.getName();
                if (ComparatorUtils.equals(XML_OPERATION_TIME, qName.getLocalPart())) {
                    time = parseContent(eventReader);
                } else if (ComparatorUtils.equals(XML_OPERATION_CONTENT, qName.getLocalPart())) {
                    content = parseContent(eventReader);
                }
                if (!ComparatorUtils.equals(time, ("")) && !ComparatorUtils.equals(content, "")) {
                    BIOperationItem biOperationItem = new BIOperationItem(time, content);
                    items.add(biOperationItem);
                    time = "";
                    content = "";
                }
            }
        }
        return items;
    }

    private String parseContent(com.fr.third.javax.xml.stream.XMLEventReader eventReader) throws Exception {
        while (eventReader.hasNext()) {
            XMLEvent currentEvent = eventReader.nextEvent();
            if (currentEvent.isCharacters()) {
                Characters characterEvent = currentEvent.asCharacters();
                String data = characterEvent.getData();
                if (!ComparatorUtils.equals(data, "\n")) {
                    return XMLEncodeUtils.cdataDecode(data);
                }
            }
        }
        return "";
    }

    private void writeFile() throws Exception {
        writeRecordFile();
    }

    private Document initialXMLDom() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = document.createElement(XML_ROOT);
        document.appendChild(root);
        if (this.createDate == null) {
            this.createDate = BIUserOperationManager.getCurrentDate();
        }
        root.setAttribute(XML_CREAT_TIME, this.createDate);
        Element reportId = document.createElement(XML_REPORT_ID);
        Element operationsNode = document.createElement(XML_OPERATIONS);
        Iterator<BIOperationItem> it = items.iterator();
        while (it.hasNext()) {
            BIOperationItem biOperationItem = it.next();
            Element item = document.createElement(XML_OPERATION_ITEM);
            Element time = document.createElement(XML_OPERATION_TIME);
            Element content = document.createElement(XML_OPERATION_CONTENT);
            time.appendChild(document.createTextNode(biOperationItem.getOperationDate()));
            content.appendChild(document.createTextNode(biOperationItem.getContent()));
            item.appendChild(time);
            item.appendChild(content);
            operationsNode.appendChild(item);
        }
        reportId.appendChild(document.createTextNode(String.valueOf(id)));
        root.appendChild(reportId);
        root.appendChild(operationsNode);
        return document;
    }

    private void writeRecordFile() throws Exception {
        PrintWriter fileWriter = new PrintWriter(filePath);
        XMLPrintWriter xmlPrintWriter = XMLPrintWriter.create(fileWriter);
        xmlPrintWriter.startTAG(XML_ROOT);
        if (this.createDate == null) {
            this.createDate = BIUserOperationManager.getCurrentDate();
        }
        fileWriter.print(" " + XML_CREAT_TIME + " =\"" + StableXMLUtils.xmlAttrEncode(this.createDate) + "\">");
        XMLWriterHelper.startTagWithNoAttribute(fileWriter, XML_REPORT_ID);
        fileWriter.print(String.valueOf(id));
        XMLWriterHelper.endTag(fileWriter, XML_REPORT_ID);

        XMLWriterHelper.startTagWithNoAttribute(fileWriter, XML_OPERATIONS);

        printItems(xmlPrintWriter);

        XMLWriterHelper.endTag(fileWriter, XML_OPERATIONS);

        XMLWriterHelper.endTag(fileWriter, XML_ROOT);
        fileWriter.close();

    }

    private void printItems(XMLPrintWriter xmlPrintWriter) {
        Iterator<BIOperationItem> it = items.iterator();

        while (it.hasNext()) {
            BIOperationItem biOperationItem = it.next();

            xmlPrintWriter.startTAG(XML_OPERATION_TIME);
            xmlPrintWriter.textNode(biOperationItem.getOperationDate());
            xmlPrintWriter.end();

            xmlPrintWriter.startTAG(XML_OPERATION_CONTENT);
            xmlPrintWriter.textNode(biOperationItem.getContent());
            xmlPrintWriter.end();


        }
    }


    /**
     * 返回字符串
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return "{" +
                "'reportName'='" + reportName + '\'' +
                ", 'userId'=" + userId +
                ", 'id'=" + id +
                ", 'items'=" + items +
                ", 'filePath'='" + filePath + '\'' +
                '}';
    }

    /**
     * 获得json数据
     *
     * @return
     * @throws Exception 异常
     */
    public JSONObject getJsonObject() throws Exception {
        JSONObject json = new JSONObject();
        json.put("reportName", reportName);
        json.put("userId", userId);
        User user = UserControl.getInstance().getUser(userId);
        if (user != null) {
            json.put("userName", user.getUsername());
        }

        json.put("id", id);
        json.put("createDate", this.createDate);
        Iterator<BIOperationItem> it = items.iterator();
        ArrayList<JSONObject> jsonItems = new ArrayList<JSONObject>();
        while (it.hasNext()) {
            JSONObject jsonItem = it.next().getJsonObject();
            jsonItems.add(jsonItem);
        }
        json.put("operations", jsonItems);
        return json;
    }

    /**
     * 获得最新的记录
     *
     * @return 最新的记录
     * @throws Exception 异常
     */
    public JSONObject getLatestConent() throws Exception {
        Iterator<BIOperationItem> it = items.iterator();
        BIOperationItem latest = null;
        while (it.hasNext()) {
            latest = it.next();
            break;
        }
        if (latest != null) {
            return new JSONObject(latest.getContent());
        } else {
            return new JSONObject();
        }
    }

    /**
     * 获得最后的记录
     *
     * @return 最新的记录
     * @throws Exception 异常
     */
    public JSONObject getLastConent() throws Exception {
        Iterator<BIOperationItem> it = items.iterator();
        if (items.size() > 1) {
            BIOperationItem latest = items.get(items.size() - 1);
            return new JSONObject(latest.getContent());
        } else {
            return new JSONObject();
        }

    }

}