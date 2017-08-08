package com.finebi.cube.location.meta;

import com.finebi.common.persist.dom.PoolDomReader;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationDomReader extends PoolDomReader<BILocationPool> {
    private final static BILogger LOGGER = BILoggerFactory.getLogger();

    public BILocationDomReader(String targetPath) {
        super(targetPath);
    }

    @Override
    public BILocationPool read() {
        BILocationPool pool = new BILocationPoolImp();
        try {
            DocumentBuilderFactory locationFactory = DocumentBuilderFactory.newInstance();
            //从DOM工厂中获得DOM解析器
            DocumentBuilder dbBuilder = locationFactory.newDocumentBuilder();
            Document doc = dbBuilder.parse(targetPath);
            NodeList locationInfo = doc.getDocumentElement().getElementsByTagName("BILocationInfo");
            for (int i = 0; i < locationInfo.getLength(); i++) {
                Element infoNode = (Element) locationInfo.item(i);
                BILocationInfo biLocationInfo = readLocationInfo(infoNode) ;
                pool.addResourceItem(biLocationInfo.getResourceName(), biLocationInfo);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return pool;
    }

    private BILocationInfo readLocationInfo(Element infoNode){
        Element locationNameEle = (Element) infoNode.getElementsByTagName("Name").item(0);
        String locationName = "emptyName";
        if (locationNameEle != null) {
            locationName = locationNameEle.getTextContent();
        }
        Element locationPathEle = (Element) infoNode.getElementsByTagName("basePath").item(0);
        String locationPath = "emptyPath";
        if (locationPathEle != null) {
            locationPath = locationPathEle.getTextContent();
        }
        Element childEle = (Element) infoNode.getElementsByTagName("child").item(0);
        String child = "emptyChild";
        if (childEle != null) {
            child = childEle.getTextContent();
        }
        return new BILocationInfoImp(locationName,locationPath,child);
    }
}
