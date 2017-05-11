package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class BIBasicTableItem implements ITableItem {

    private String dId;
    private String text;
    private JSONArray values;
    private boolean needExpand;
    private boolean isExpanded;
    protected List<ITableItem> children;
    private ITableStyle style;
    private String type;
    private String value;

    public BIBasicTableItem() {
    }

    public void setDId(String dId) {
        this.dId = dId;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setValues(JSONArray value) {
        this.values = value;
    }

    public void setNeedExpand(boolean needExpand) {
        this.needExpand = needExpand;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public void setChildren(List<ITableItem> children) {
        this.children = children;
    }

    @Override
    public boolean hasValues() {
        return null != values && values.length() > 0;
    }

    public void setStyle(ITableStyle style) {
        this.style = style;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getDId() {
        return dId;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public JSONArray getValues() {
        return values;
    }

    public boolean isNeedExpand() {
        return needExpand;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public List<ITableItem> getChildren() {
        return children;
    }

    public ITableStyle getStyle() {
        return style;
    }

    public String getType() {
        return type;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("dId")) {
            dId = jo.optString("dId");
        }
        if (jo.has("text")) {
            text = jo.optString("text");
        }
//        if (jo.has("type")) {
//            type = jo.optString("type");
//        }
        if (jo.has("values")) {
            values = jo.optJSONArray("values");
        }
        if (jo.has("value")) {
            value = jo.optString("value");
        }

        if (jo.has("children")) {
            children=new ArrayList<ITableItem>();
            for (int i = 0; i < jo.getJSONArray("children").length(); i++) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.parseJSON(jo.getJSONArray("children").getJSONObject(i));
                children.add(item);
            }
        }

    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        if (null != this.children&&children.size()>0l) {
            JSONArray children = new JSONArray();
            for (ITableItem item : this.children) {
                children.put(item.createJSON());
            }
            jo.put("children", children);
        }
        jo.put("dId", dId);
//        jo.put("styles", null == style ? new JSONObject() : style.createJSON());
        jo.put("text", text);
//        jo.put("type", type);
        jo.put("values", values);
        jo.put("value",value);
        return jo;
    }

}
