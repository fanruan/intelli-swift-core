package com.fr.bi.conf.report.style;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONObject;
import com.fr.json.JSONParser;
import com.fr.stable.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by User on 2016/9/6.
 * chart get的setting属性
 */
public class DetailChartSetting implements JSONParser {

    private JSONObject settings = JSONObject.create();
    private JSONObject dimensions = JSONObject.create();
    private Map<String, Integer> numberLevelMap = new HashMap<String, Integer>();
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("settings")) {
            settings = jo.getJSONObject("settings");
        }
        if (jo.has("dimensions")) {
            dimensions = jo.getJSONObject("dimensions");
        }
        Iterator it = dimensions.keys();
        while (it.hasNext()){
            String id = it.next().toString();
            numberLevelMap.put(id, getTargetSettingsByTarget(id).optInt("num_level", BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL));
        }
    }

    public JSONObject getDetailChartSetting() {
        return settings;
    }

    public boolean showRowTotal() {
        return settings.optBoolean("show_row_total", true);
    }

    public boolean showColTotal() {
        return settings.optBoolean("show_col_total", true);
    }

    public int getNumberLevelByTargetId(String targetId) {
        Integer level = numberLevelMap.get(targetId);
        return level == null ? BIReportConstant.TARGET_STYLE.NUM_LEVEL.NORMAL : level;
    }

    public int getFormatByTargetId(String targetId) {
        return getTargetSettingsByTarget(targetId).optInt("format", BIReportConstant.TARGET_STYLE.FORMAT.NORMAL);
    }

    private JSONObject getTargetSettingsByTarget (String targetId) {
        JSONObject targetSettings = new JSONObject();
        if (dimensions.has(targetId)) {
            JSONObject target = dimensions.optJSONObject(targetId);
            if (target.has("settings")) {
                targetSettings = target.optJSONObject("settings");
            }
        }
        return  targetSettings;
    }

    public String getUnitByTargetId(String targetId) {
        return getTargetSettingsByTarget(targetId).optString("unit", StringUtils.EMPTY);
    }
}
