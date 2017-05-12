package com.fr.bi.cal.analyze.report.report.widget.chart.export.format;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/5/11.
 */
public class TableFormatSetting  implements FormatSetting{

    private String unit;
    private boolean numSeparators;
    private int numberLevel;
    private int format;
    private JSONArray conditions;
    private int iconStyle;
    private int mark;

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public boolean isNumSeparators() {
        return numSeparators;
    }

    @Override
    public int getNumberLevel() {
        return numberLevel;
    }

    @Override
    public int getFormat() {
        return format;
    }

    @Override
    public JSONArray getConditions() {
        return conditions;
    }

    @Override
    public int getIconStyle() {
        return iconStyle;
    }

    @Override
    public int getMark() {
        return mark;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        return null;
    }
    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("unit")){
            unit=jo.optString("unit");
        }
        if (jo.has("numSeparators")){
            numSeparators=jo.optBoolean("numSeparators");
        }
        if (jo.has("numLevel")){
            numberLevel=jo.optInt("numLevel");
        }
        if (jo.has("format")){
            format=jo.optInt("format");
        }
        if (jo.has("conditions")){
            conditions=jo.optJSONArray("conditions");
        }
        if (jo.has("iconStyle")){
            iconStyle=jo.optInt("iconStyle");
        }
        if (jo.has("mark")){
            mark=jo.optInt("mark");
        }

    }
}
