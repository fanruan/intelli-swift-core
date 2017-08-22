package com.fr.bi.fs;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by wang on 2017/4/20.
 */
public interface BIReportQueryProvider {
    String XML_TAG = "BIReportQuery";

    JSONObject getAllHangoutReports(long userId, String currentUser);

    JSONArray getReportAndFolder(long userId);

    JSONObject getAllReportsData(long userId);
}
