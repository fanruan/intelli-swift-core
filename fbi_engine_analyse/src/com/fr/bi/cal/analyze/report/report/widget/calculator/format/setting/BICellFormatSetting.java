package com.fr.bi.cal.analyze.report.report.widget.calculator.format.setting;

import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/5/11.
 */
public class BICellFormatSetting implements ICellFormatSetting {

    private String unit;
    private boolean numSeparators;
    private int numberLevel;
    private int format;
    private JSONArray conditions;
    private int iconStyle;
    private int mark;
    private int dateFormatType;
    private boolean hasSeparator;

    public BICellFormatSetting() {
        hasSeparator = true;
        numSeparators = true;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = JSONObject.create();
        jo.put("unit", unit);
        jo.put("numSeparators", numSeparators);
        jo.put("numLevel", numberLevel);
        jo.put("formatDecimal", format);
        jo.put("conditions", conditions);
        jo.put("iconStyle", iconStyle);
        jo.put("mark", mark);
        jo.put("hasSeparator", hasSeparator);
        jo.put("dateFormat", new JSONObject().put("type", dateFormatType));
        return jo;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("unit")) {
            unit = jo.optString("unit");
        }
        if (jo.has("numSeparators")) {
            numSeparators = jo.optBoolean("numSeparators");
        }
        if (jo.has("numLevel")) {
            numberLevel = jo.optInt("numLevel");
        }
        if (jo.has("iconStyle")) {
            iconStyle = jo.optInt("iconStyle");
        }
        format = BIReportConstant.TARGET_STYLE.FORMAT.NORMAL;
        if (jo.has("formatDecimal")) {
            format = jo.optInt("formatDecimal");
        }
        if (jo.has("conditions")) {
            conditions = jo.optJSONArray("conditions");
        }
        if (jo.has("iconStyle")) {
            iconStyle = jo.optInt("iconStyle");
        }
        if (jo.has("hasSeparator")) {
            hasSeparator = jo.optBoolean("hasSeparator");
        }

        if (jo.has("mark")) {
            mark = jo.optInt("mark");
        }
        if (jo.has("dateFormat") && jo.getJSONObject("dateFormat").has("type")) {
            dateFormatType = jo.getJSONObject("dateFormat").optInt("type");
        }
    }
}
