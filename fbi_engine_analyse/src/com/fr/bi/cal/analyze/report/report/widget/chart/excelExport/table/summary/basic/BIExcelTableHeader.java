package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.BIExcelHeader;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/13.
 */
public class BIExcelTableHeader implements BIExcelHeader {
    private String text;
    private String title;
    private String tag;
    private String dID;
    private JSONObject style;

    public BIExcelTableHeader(String text, String title, String tag, String dID, JSONObject style) {
        this.text = text;
        this.title = title;
        this.tag = tag;
        this.dID = dID;
        this.style = style;
    }

    public BIExcelTableHeader() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getdID() {
        return dID;
    }

    public void setdID(String dID) {
        this.dID = dID;
    }

    public JSONObject getStyle() {
        return style;
    }

    public void setStyle(JSONObject style) {
        this.style = style;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("title", getTitle());
        jo.put("tag", getTag());
        jo.put("dId", getdID());
        jo.put("style", getStyle());
        return jo;
    }
}
