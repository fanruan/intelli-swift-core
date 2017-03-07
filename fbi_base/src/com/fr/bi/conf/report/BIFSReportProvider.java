package com.fr.bi.conf.report;

import com.fr.bi.fs.BIDesignReport;
import com.fr.bi.fs.BIReportNode;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by wang on 2016/12/5.
 */
public interface BIFSReportProvider {
    String XML_TAG = "BIFSReport";

    long createNewBIReport(BIDesignReport BIReport, long userId, String reportName, String parentId, String description) throws Exception;

    long updateExistBIReport(BIDesignReport BIReport, long userId, String sessionId) throws Exception;

    JSONArray getAllFoldersAndReports(long userId) throws Exception;

    long doSaveFileAndObject(BIDesignReport BIReport, BIReportNode node, long userId) throws Exception;
    BIReportNode findReportNode(long reportId, long userId) throws Exception;
    JSONObject getBIReportNodeJSON(BIReportNode node) throws Exception;
}
