package com.fr.swift.conf.business.table;

import com.finebi.base.constant.BaseConstant;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.fr.stable.StringUtils;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.List;

/**
 * This class created on 2018-1-23
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TableXmlWriter implements ISwiftXmlWriter<FineBusinessTable> {
    public void write(List<FineBusinessTable> tables, OutputStream outputStream) throws Exception {

        TransformerHandler handler = null;
        String fileName;
        String rootElement;
        SAXTransformerFactory fac = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        handler = fac.newTransformerHandler();
        Transformer transformer = handler.getTransformer();
        //设置输出采用的编码方式
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        //是否自动添加额外的空白
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //是否忽略xml声明
        Result resultxml = new StreamResult(outputStream);
        handler.setResult(resultxml);
        AttributesImpl attrs = new AttributesImpl();
        handler.startDocument();
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "businessTable", attrs);
        for (FineBusinessTable temp : tables) {
            // 对tableType进行判断，不同的tableType，不同的写入方法
            switch (temp.getType()) {
                case BaseConstant.TABLETYPE.DB:
                    writeDBTable(temp, attrs, handler);
                    break;
                case BaseConstant.TABLETYPE.SQL:
                    writeSQLTable(temp, attrs, handler);
            }
        }
        handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "businessTable");
        handler.endDocument();
    }

    private void writeDBTable(FineBusinessTable dbTable, AttributesImpl attrs, TransformerHandler handler) throws SAXException {
        FineDBBusinessTable table = (FineDBBusinessTable) dbTable;
        attrs.clear();
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "id", StringUtils.EMPTY, table.getId());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "name", StringUtils.EMPTY, table.getName());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "engineType", StringUtils.EMPTY, String.valueOf(table.getEngineType().getType()));
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "connName", StringUtils.EMPTY, table.getConnName());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "tableType", StringUtils.EMPTY, String.valueOf(table.getType()));
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "Table", attrs);
        List<FineBusinessField> fields = table.getFields();
        writeFields(fields, attrs, handler);
        handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "Table");
    }

    private void writeSQLTable(FineBusinessTable sqlTable, AttributesImpl attrs, TransformerHandler handler) throws SAXException {
        FineSQLBusinessTable table = (FineSQLBusinessTable) sqlTable;
        attrs.clear();
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "id", StringUtils.EMPTY, table.getId());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "name", StringUtils.EMPTY, table.getName());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "engineType", StringUtils.EMPTY, String.valueOf(table.getEngineType().getType()));
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "sql", StringUtils.EMPTY, table.getSql());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "connName", StringUtils.EMPTY, table.getConnName());
        attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "tableType", StringUtils.EMPTY, String.valueOf(table.getType()));
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "Table", attrs);
        List<FineBusinessField> fields = table.getFields();
        writeFields(fields, attrs, handler);
        handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "Table");
    }

    private void writeFields(List<FineBusinessField> fields, AttributesImpl attrs, TransformerHandler handler) throws SAXException {
        for (FineBusinessField field : fields) {
            attrs.clear();
            // TODO  这边要保存field对象，构造的情况是什么样的？
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "fieldId", StringUtils.EMPTY, field.getId());
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "fieldName", StringUtils.EMPTY, field.getName());
            //attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "transferName", StringUtils.EMPTY, field.getTransferName());
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "fieldType", StringUtils.EMPTY, String.valueOf(field.getType()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "fieldSize", StringUtils.EMPTY, String.valueOf(field.getSize()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "isUsed", StringUtils.EMPTY, String.valueOf(field.isEnable()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "isUsable", StringUtils.EMPTY, String.valueOf(field.isUsable()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "isPrimaryKey", StringUtils.EMPTY, String.valueOf(field.isPrimaryKey()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "engineType", StringUtils.EMPTY, String.valueOf(field.getEngineType().getType()));
            handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "field", attrs);
            handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "field");
        }
    }
}
