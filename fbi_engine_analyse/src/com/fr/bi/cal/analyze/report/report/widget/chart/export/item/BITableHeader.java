package com.fr.bi.cal.analyze.report.report.widget.chart.export.item;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.BITableItemStyle;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.style.ITableStyle;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by Kary on 2017/2/13.
 */
public class BITableHeader implements ITableHeader {
    private String text;
    private String title;
    private String tag;
    private String dID;
    private ITableStyle style;
    private String type;
    private boolean isUsed;

    public BITableHeader(String text, String title, String tag, String dID, ITableStyle style, boolean isUsed) {
        this.text = text;
        this.title = title;
        this.tag = tag;
        this.dID = dID;
        this.style = style;
        this.isUsed = isUsed;
    }

    public BITableHeader() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public ITableStyle getStyle() {
        return style;
    }

    public void setStyle(BITableItemStyle style) {
        this.style = style;
    }

    @Override
    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("title", getTitle());
        jo.put("tag", getTag());
        jo.put("dId", getdID());
        jo.put("style", null != getStyle() ? getStyle().createJSON() : "");
        jo.put("text", getText());
        jo.put("type", getType());
        jo.put("isUsed", isUsed());
        return jo;
    }

    @Override
    public void parseJson(JSONObject json) throws JSONException {
        if (json.has("title")) {
            setTitle(json.getString("title"));
        }
        if (json.has("tag")) {
            setTag(json.getString("tag"));
        }
        if (json.has("dId")) {
            setdID(json.getString("dId"));
        }
        if (json.has("style")) {
            BITableItemStyle style = new BITableItemStyle();
            style.parse(json.getJSONObject("style"));
            setStyle(style);
        }
        if (json.has("text")) {
            setText(json.getString("text"));
        }
        if (json.has("type")) {
            setType(json.getString("type"));
        }
        setUsed(json.optBoolean("used", true));
    }
}
