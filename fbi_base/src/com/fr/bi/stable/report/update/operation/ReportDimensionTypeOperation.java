package com.fr.bi.stable.report.update.operation;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Kary on 2017/4/11.
 * 维度类型需要转换
 */
public class ReportDimensionTypeOperation implements ReportUpdateOperation {
    @Override
    public JSONObject update(JSONObject reportSetting) throws JSONException {
        boolean checkDimensionsExist = reportSetting.has("widgets") || reportSetting.getJSONObject("widgets").length() > 0;
        if (checkDimensionsExist) {
            JSONObject widgets = reportSetting.getJSONObject("widgets");
            Iterator widgetKeys = widgets.keys();
            while (widgetKeys.hasNext()) {
                String widgetId = widgetKeys.next().toString();
                JSONObject widget = widgets.getJSONObject(widgetId);
                if (widget.has("dimensions") && widget.length() > 0) {
                    JSONObject dimensions = widget.getJSONObject("dimensions");
                    Iterator dimKeys = dimensions.keys();
                    while (dimKeys.hasNext()) {
                        String dimensionId = dimKeys.next().toString();
                        if (dimensions.getJSONObject(dimensionId).has("type") && dimensions.getJSONObject(dimensionId).get("type") instanceof String) {
                            JSONObject tempDimension = new JSONObject(dimensions.getJSONObject(dimensionId).toString());
                            tempDimension.put("type", Integer.valueOf(dimensions.getJSONObject(dimensionId).getString("type")));
                            dimensions.put(dimensionId, tempDimension);
                        }
                    }
                    widget.put("dimensions", dimensions);
                }
            }
        }
        return reportSetting;
    }
}
