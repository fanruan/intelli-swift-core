package com.fr.swift.conf.business.table;

import com.finebi.base.constant.BaseConstant;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.internalimp.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.table.FineSQLBusinessTable;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.AbstractSwiftParseXml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-23
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TableParseXml extends AbstractSwiftParseXml<FineBusinessTable> {

    private String tagName;

    @Override
    public void startDocument() throws SAXException {

        list = new ArrayList<FineBusinessTable>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (ComparatorUtils.equals(qName, "Table")) {
            // 对tableType进行判断，不同类型的table，不同类型的读取
            switch (Integer.valueOf(attributes.getValue("tableType"))) {
                case BaseConstant.TABLETYPE.DB:
                    parseDBTable(attributes);
                    break;
                case BaseConstant.TABLETYPE.SQL:
                    parseSQLTable(attributes);
                    break;
            }
        } else if (ComparatorUtils.equals(qName, "field")) {
            FineBusinessFieldImp field = new FineBusinessFieldImp();
            field.setId(attributes.getValue("fieldId"));
            field.setName(attributes.getValue("fieldName"));
            field.setEnable(Boolean.valueOf(attributes.getValue("isUsable")));
            field.setPrimaryKey(Boolean.valueOf(attributes.getValue("isPrimaryKey")));
            field.setEngineType(FineEngineType.getEngineType(Integer.valueOf(attributes.getValue("engineType"))));
            field.setSize((Integer.valueOf(attributes.getValue("fieldSize"))));
            field.setType((Integer.valueOf(attributes.getValue("fieldType"))));
            field.setUsable(Boolean.valueOf(attributes.getValue("isUsed")));
            field.setTransferName(attributes.getValue("transferName"));
            resource.addField(field);
        }
        this.tagName = qName;
    }

    private void parseDBTable(Attributes attributes) {
        String name = attributes.getValue("name");
        String tableName = attributes.getValue("name");
        String id = attributes.getValue("id");
        FineEngineType engineType = FineEngineType.getEngineType(Integer.valueOf(attributes.getValue("engineType")));
        String connName = attributes.getValue("connName");
        resource = new FineDBBusinessTable(name, engineType, connName, tableName);
        resource.setId(id);
    }

    private void parseSQLTable(Attributes attributes) {
        String name = attributes.getValue("name");
        String id = attributes.getValue("id");
        FineEngineType engineType = FineEngineType.getEngineType(Integer.valueOf(attributes.getValue("engineType")));
        String sql = attributes.getValue("sql");
        String connName = attributes.getValue("connName");
        resource = new FineSQLBusinessTable(name, connName, engineType, sql);
        resource.setId(id);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (ComparatorUtils.equals(qName, "Table")) {

            this.list.add(this.resource);
        }
        this.tagName = null;
    }

    @Override
    public void endDocument() throws SAXException {
        if (ComparatorUtils.equals(tagName, "Table")) {
            this.list.add(this.resource);
        }
        this.tagName = null;

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        if (this.tagName != null) {
            String date = new String(ch, start, length);
            if (ComparatorUtils.equals(tagName, "table")) {
            }
        }
    }

    public List<FineBusinessTable> getList() throws Exception {

        List<FineBusinessTable> ret = new ArrayList<FineBusinessTable>();
        if (list != null) {
            for (FineBusinessTable table : list) {
                ret.add(table);
            }
        }
        return ret;
    }

}
