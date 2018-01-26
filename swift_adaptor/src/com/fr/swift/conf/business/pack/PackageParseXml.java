package com.fr.swift.conf.business.pack;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.pack.FineBusinessPackageImp;
import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.fr.general.ComparatorUtils;
import com.fr.swift.conf.business.AbstractSwiftParseXml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * defaultHandler
 * This class created on 2018-1-23
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class PackageParseXml extends AbstractSwiftParseXml<FineBusinessPackage> {

    private String tagName;

    public PackageParseXml() {
    }

    @Override
    public void startDocument() {
        list = new ArrayList<FineBusinessPackage>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (ComparatorUtils.equals(qName, "Package")) {
            String packageId = attributes.getValue("packageId");
            String packageName = attributes.getValue("packageName");
            long owner = Long.parseLong(attributes.getValue("owner"));
            long initTime = Long.parseLong(attributes.getValue("initTime"));
            FineEngineType engineType = FineEngineType.valueOf(attributes.getValue("engineType"));
            resource = new FineBusinessPackageImp(packageId, packageName, owner, initTime, engineType, initTime);
        }
        this.tagName = qName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {

        if (ComparatorUtils.equals(qName, "Package")) {
            this.list.add(this.resource);
        }
        this.tagName = null;
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (this.tagName != null) {
            String date = new String(ch, start, length);
            if (ComparatorUtils.equals(tagName, "table")) {
                this.resource.addTable(date);
            }
        }
    }
}
