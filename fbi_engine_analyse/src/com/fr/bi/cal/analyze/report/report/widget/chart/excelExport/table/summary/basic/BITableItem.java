package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.BIExcelItemData;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class BITableItem implements BIExcelItemData {

    private String dId;
    private String text;
    private JSONArray value;
    private boolean isCross;
    private boolean needExpand;
    private boolean isExpanded;
    private List<BITableItem> children;
    private JSONArray clicked;
    private boolean isSum;
    private String style;
    private String type;
    private String tag;

    public BITableItem() {
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public void setText(String text) {
        this.text = text;
    }

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

    public void setChildren(List<BITableItem> children) {
        this.children = children;
    }

    public void setClicked(JSONArray clicked) {
        this.clicked = clicked;
    }

    public void setSum(boolean sum) {
        isSum = sum;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getdId() {
        return dId;
    }

    public String getText() {
        return text;
    }

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

    public List<BITableItem> getChildren() {
        return children;
    }

    public JSONArray getClicked() {
        return clicked;
    }

    public boolean isSum() {
        return isSum;
    }

    public String getStyle() {
        return style;
    }

    public String getType() {
        return type;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "BIExcelTableItem{" +
                "dId='" + dId + '\'' +
                ", text='" + text + '\'' +
                ", value=" + value +
                ", isCross=" + isCross +
                ", needExpand=" + needExpand +
                ", isExpanded=" + isExpanded +
                ", children=" + children +
                ", clicked=" + clicked +
                ", isSum=" + isSum +
                ", style='" + style + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public JSONObject createJSON() throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("dId", dId);
        jo.put("styles", style);
        jo.put("text", text);
        jo.put("type", type);
        jo.put("values", value);
        jo.put("children", children);
        jo.put("isSum",isSum);
        jo.put("isCross",isCross);
        return jo;
    }

}
