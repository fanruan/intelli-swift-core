package com.finebi.cube.location.meta;

import com.finebi.common.persist.dom.PoolDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationDomWriter extends PoolDomWriter<BILocationPool> {
    public BILocationDomWriter(String targetPath){
        super(targetPath);
    }

    @Override
    public Document buildDocument(BILocationPool pool, Element documentElement) {
        Element locationInfoPool = document.createElement("BILocationInfoPool");
        for (BILocationInfo ele : pool.getAllItems()) {
            Element locationInfo = document.createElement("BILocationInfo");
            Element locationName = document.createElement("Name");
            locationName.setTextContent(ele.getName().value());
            locationInfo.appendChild(locationName);

            Element locationPath = document.createElement("basePath");
            locationPath.setTextContent(ele.getBasePath());
            locationInfo.appendChild(locationPath);

            Element child = document.createElement("child");
            child.setTextContent(ele.getChild());
            locationInfo.appendChild(child);

            locationInfoPool.appendChild(locationInfo);
        }
        documentElement.appendChild(locationInfoPool);
        return document;
    }
}
