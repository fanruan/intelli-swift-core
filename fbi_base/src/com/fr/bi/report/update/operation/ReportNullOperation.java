package com.fr.bi.report.update.operation;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by kary on 2017/1/23.
 */
public class ReportNullOperation implements ReportUpdateOperation {

    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        return reportSetting;
    }
}
