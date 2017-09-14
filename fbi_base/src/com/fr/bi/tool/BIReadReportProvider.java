package com.fr.bi.tool;

import com.fr.bi.fs.BIReportNode;
import com.fr.json.JSONObject;

/**
 * Created by wang on 2016/12/5.
 */
public interface BIReadReportProvider {
    String XML_TAG = "BIReadReport";

    JSONObject getBIReportNodeJSON(BIReportNode node) throws Exception;

    boolean isNodeValid(BIReportNode node) throws Exception;
}
