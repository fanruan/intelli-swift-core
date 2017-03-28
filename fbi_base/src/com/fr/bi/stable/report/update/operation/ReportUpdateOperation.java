package com.fr.bi.stable.report.update.operation;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by kary on 2017/2/4.
 */
public interface ReportUpdateOperation {
     JSONObject update(JSONObject reportSetting) throws JSONException;
}
