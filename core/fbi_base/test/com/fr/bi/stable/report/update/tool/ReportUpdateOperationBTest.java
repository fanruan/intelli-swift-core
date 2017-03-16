package com.fr.bi.stable.report.update.tool;

import com.fr.bi.stable.report.update.operation.ReportSettingsUpdateOperation;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/4.
 */
public class ReportUpdateOperationBTest implements ReportSettingsUpdateOperation {
    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        return reportSetting.put("version2.0", "");
    }
}
