package com.fr.bi.cal.analyze.report.report.widget.chart.export.basic;

import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

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
    private ITableStyle style;
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

    public void setStyle(ITableStyle style) {
        this.style = style;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDId() {
        return dId;
    }

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

    public ITableStyle getStyle() {
        return style;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONArray childs = new JSONArray();
        if (null != children) {
            for (ITableItem item : children) {
                childs.put(item.createJSON());
            }
        }
        JSONObject jo = new JSONObject();
        jo.put("dId", dId);
        jo.put("styles", style);
        jo.put("text", text);
        jo.put("type", type);
        jo.put("values", value);
        jo.put("children", childs);
        jo.put("isSum", isSum);
        jo.put("isCross", isCross);
        return jo;
    }

}
