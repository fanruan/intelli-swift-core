package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class ReportItem {

    private String dId;
    private String text;
    private List<ReportItem> value;
    private boolean isCross;
    private boolean needExpand;
    private boolean isExpanded;
    private List<ReportItem> children;
    private List<String> clicked;
    private boolean isSum;
    private String style;
    private String type;

    public ReportItem() {
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

    public List<ReportItem> getValue() {
        return value;
    }

    public void setValue(List<ReportItem> value) {
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

    public List<ReportItem> getChildren() {
        return children;
    }

    public void setChildren(List<ReportItem> children) {
        this.children = children;
    }

    public List<String> getClicked() {
        return clicked;
    }

    public void setClicked(List<String> clicked) {
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

    @Override
    public String toString() {
        return "ReportItem{" +
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
}
