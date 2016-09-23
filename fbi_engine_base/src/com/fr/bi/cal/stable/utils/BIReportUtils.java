package com.fr.bi.cal.stable.utils;

import com.finebi.cube.conf.field.BIBusinessField;
import com.finebi.cube.conf.field.BusinessField;
import com.fr.bi.fs.BIReportNode;
import com.fr.bi.stable.constant.BIJSONConstant;
import com.fr.bi.stable.data.BIFieldID;
import com.fr.bi.tool.BIReadReportUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BIReportUtils {

    /**
     * daniel
     * FIXME 还需要考虑 控件和指标过滤
     *
     * @param widget
     * @return
     * @throws JSONException
     */
    private static JSONArray getAllFields(JSONObject widget) throws JSONException {
        JSONArray res = new JSONArray();
        if (widget.has("dimensions")) {
            JSONObject ja = widget.getJSONObject("dimensions");
            Iterator it = ja.keys();
            while (it.hasNext()) {
                res.put(ja.get(it.next().toString()));
            }
        }
        return res;
    }

    public static Set<BusinessField> getUsedFieldByReportNode(BIReportNode node, long userId) throws Exception {
        JSONObject reportSetting = BIReadReportUtils.getBIReportNodeJSON(node);
        JSONObject widgets = reportSetting.getJSONObject("widgets");
        Set<BusinessField> fields = new HashSet<BusinessField>();
        Iterator it = widgets.keys();
        while (it.hasNext()) {
            JSONObject widget = widgets.getJSONObject(it.next().toString());
            JSONArray targets = getAllFields(widget);
            for (int j = 0; j < targets.length(); j++) {
                JSONObject target = targets.getJSONObject(j);
                if (target.has(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT)) {
                    JSONObject fieldJo = target.getJSONObject(BIJSONConstant.JSON_KEYS.STATISTIC_ELEMENT);
                    BusinessField field = new BIBusinessField(new BIFieldID(fieldJo.getString("field_id")));
                    fields.add(field);
                }
            }
        }
        return fields;
    }
}