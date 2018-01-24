package com.fr.swift.conf.business.pack;

import com.finebi.conf.structure.bean.pack.FineBusinessPackage;
import com.fr.swift.conf.business.AbstractSwiftParseXml;
import com.fr.swift.conf.business.SwiftBaseXmlDao;
import com.fr.swift.conf.business.ISwiftXmlWriter;

/**
 * This class created on 2018-1-23 16:43:12
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftPackageDao extends SwiftBaseXmlDao<FineBusinessPackage> {

    public SwiftPackageDao(AbstractSwiftParseXml<FineBusinessPackage> xmlHandler, String xmlFileName, ISwiftXmlWriter<FineBusinessPackage> swiftXmlWriter) {
        super(xmlHandler, xmlFileName, swiftXmlWriter);
    }
}
