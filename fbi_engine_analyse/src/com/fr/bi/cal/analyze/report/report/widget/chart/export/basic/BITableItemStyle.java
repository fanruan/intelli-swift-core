package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/26.
 */
public class BITableItemStyle implements ITableStyle {
    private String background;
    private String color;
    private String fontWeight;
    private String filter;

    public BITableItemStyle(String background, String color, String fontWeight) {
        this.background = background;
        this.color = color;
        this.fontWeight = fontWeight;
    }

    public BITableItemStyle(String background, String color, String fontWeight, String filter) {
        this.background = background;
        this.color = color;
        this.fontWeight = fontWeight;
        this.filter = filter;
    }

    public BITableItemStyle() {

    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("background", background);
        jo.put("color", color);
        jo.put("fontWeight", fontWeight);
        if (filter != null) {
            jo.put("filter", filter);
        }
        return jo;
    }

    @Override
    public void parse(JSONObject style) {

    }
}
