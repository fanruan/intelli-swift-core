package com.finebi.cube.location.meta;

import com.finebi.common.persist.dom.PoolDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by wang on 2017/6/20.
 */
public class BILocationDomWriter extends PoolDomWriter<BILocationPool> {
    public BILocationDomWriter(String targetPath) {
        super(targetPath);
    }

    @Override
    public Document buildDocument(BILocationPool pool, Element documentElement) {
        Element locationInfoPool = document.createElement("BILocationInfoPool");
        for (BILocationInfo ele : pool.getAllItems()) {
            Element locationInfo = document.createElement("BILocationInfo");

            Element baseFolder = document.createElement("baseFolder");
            baseFolder.setTextContent(ele.getCubeFolder());
            locationInfo.appendChild(baseFolder);

            Element logicFolder = document.createElement("logicFolder");
            logicFolder.setTextContent(ele.getLogicFolder());
            locationInfo.appendChild(logicFolder);

            Element child = document.createElement("child");
            child.setTextContent(ele.getChild());
            locationInfo.appendChild(child);

            locationInfoPool.appendChild(locationInfo);
        }
        documentElement.appendChild(locationInfoPool);
        return document;
    }
}
