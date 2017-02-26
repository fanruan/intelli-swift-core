package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.summary.basic;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic.BIExcelItemData;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class BIExcelTableItem implements BIExcelItemData{

    private String dId;
    private String text;
    private JSONArray value;
    private boolean isCross;
    private boolean needExpand;
    private boolean isExpanded;
    private List<BIExcelTableItem> children;
    private JSONArray clicked;
    private boolean isSum;
    private String style;
    private String type;
    private String tag;

    public BIExcelTableItem() {
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JSONArray getValue() {
        return value;
    }

    public void setValue(JSONArray value) {
        this.value = value;
    }

    public boolean isCross() {
        return isCross;
    }

    public void setCross(boolean cross) {
        isCross = cross;
    }

    public boolean isNeedExpand() {
        return needExpand;
    }

    public void setNeedExpand(boolean needExpand) {
        this.needExpand = needExpand;
    }

    public List<BIExcelTableItem> getChildren() {
        return children;
    }

    public void setChildren(List<BIExcelTableItem> children) {
        this.children = children;
    }

    public JSONArray getClicked() {
        return clicked;
    }

    public void setClicked(JSONArray clicked) {
        this.clicked = clicked;
    }

    public boolean isSum() {
        return isSum;
    }

    public void setSum(boolean sum) {
        isSum = sum;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
        jo.put("dId",getdId());
        jo.put("styles",getStyle());
        jo.put("text",getText());
        jo.put("type",getType());
        jo.put("values",getValue());
        return jo;
    }

}
