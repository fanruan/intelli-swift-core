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
    private JSONArray value;
    private boolean isCross;
    private boolean needExpand;
    private boolean isExpanded;
    protected List<ITableItem> children;
    private JSONArray clicked;
    private boolean isSum;
    private ITableStyle styles;
    private String type;
    private String tag;

    public BIBasicTableItem() {
    }

    public void setDId(String dId) {
        this.dId = dId;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setValue(JSONArray value) {
        this.value = value;
    }

    public void setCross(boolean cross) {
        isCross = cross;
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
        return null != value && value.length() > 0;
    }

    public void setClicked(JSONArray clicked) {
        this.clicked = clicked;
    }

    public void setSum(boolean sum) {
        isSum = sum;
    }

    public void setStyles(ITableStyle styles) {
        this.styles = styles;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
    public JSONArray getValue() {
        return value;
    }

    public boolean isCross() {
        return isCross;
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

    public JSONArray getClicked() {
        return clicked;
    }

    public boolean isSum() {
        return isSum;
    }

    public ITableStyle getStyles() {
        return styles;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public void parseJSON(JSONObject jo) throws Exception {
        if (jo.has("dId")) {
            dId = jo.optString("dId");
        }
        if (jo.has("dId")) {
            dId = jo.optString("dId");
        }
        if (jo.has("text")) {
            text = jo.optString("text");
        }
        if (jo.has("type")) {
            type = jo.optString("type");
        }
        if (jo.has("values")) {
            value = jo.optJSONArray("values");
        }
        if (jo.has("children")) {
            children=new ArrayList<ITableItem>();
            for (int i = 0; i < jo.getJSONArray("children").length(); i++) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.parseJSON(jo.getJSONArray("children").getJSONObject(i));
                children.add(item);
            }
        }
        if (jo.has("isSum")) {
            isSum = jo.optBoolean("isSum");
        }
        if (jo.has("isCross")) {
            isCross = jo.optBoolean("isCross");
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
        jo.put("styles", null == styles ? new JSONObject() : styles.createJSON());
        jo.put("text", text);
        jo.put("type", type);
        jo.put("values", value);
        jo.put("isSum", isSum);
        jo.put("isCross", isCross);
        return jo;
    }

}
