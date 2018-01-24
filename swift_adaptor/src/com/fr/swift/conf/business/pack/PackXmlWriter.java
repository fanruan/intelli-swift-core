package com.fr.swift.conf.business.pack;

import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.fr.stable.StringUtils;
import com.fr.swift.conf.business.ISwiftXmlWriter;
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
public class PackXmlWriter implements ISwiftXmlWriter<FineBusinessPackage> {

    @Override
    public void write(List<FineBusinessPackage> packs, OutputStream outputStream) throws Exception {

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
        handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "businessPack", attrs);

        for (FineBusinessPackage pack : packs) {
            attrs.clear();
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "packageId", StringUtils.EMPTY, pack.getId());
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "packageName", StringUtils.EMPTY, pack.getName());
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "owner", StringUtils.EMPTY, String.valueOf(pack.getOwner()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "initTime", StringUtils.EMPTY, String.valueOf(pack.getInitTime()));
            attrs.addAttribute(StringUtils.EMPTY, StringUtils.EMPTY, "engineType", StringUtils.EMPTY, String.valueOf(pack.getEngineType()));
            handler.startElement("", "", "Package", attrs);
            // TODO 在这里记录表
            List<String> tables = pack.getTables();
            for (String tableId : tables) {
                attrs.clear();
                handler.startElement(StringUtils.EMPTY, StringUtils.EMPTY, "table", attrs);
                handler.characters(tableId.toCharArray(), 0, tableId.length());
                handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "table");
            }
            handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "Package");
        }
        handler.endElement(StringUtils.EMPTY, StringUtils.EMPTY, "businessPack");
        handler.endDocument();
    }
}
