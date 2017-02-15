package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.table;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class ReportExportItem {

    private String dId;
    private String text;
    private List<ReportExportItem> value;
    private boolean isCross;
    private boolean needExpand;
    private List<ReportExportItem> children;
    private List<String> clicked;
    private boolean isSum;
    private String style;
    private String type;

    public ReportExportItem() {
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

    public List<ReportExportItem> getValue() {
        return value;
    }

    public void setValue(List<ReportExportItem> value) {
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

    public List<ReportExportItem> getChildren() {
        return children;
    }

    public void setChildren(List<ReportExportItem> children) {
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

    @Override
    public String toString() {
        return "ReportExportItem{" +
                "dId='" + dId + '\'' +
                ", text='" + text + '\'' +
                ", value=" + value +
                ", isCross=" + isCross +
                ", needExpand=" + needExpand +
                ", children=" + children +
                ", clicked=" + clicked +
                ", isSum=" + isSum +
                ", style='" + style + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
