package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.table;

import java.util.List;

/**
 * Created by Kary on 2017/2/13.
 */
public class ReportExportItem {

    private String dId;
    private String text;
    private String[] value;
    private boolean isCross;
    private boolean needExpand;
    private List<ReportExportItem> children;
    private List<ReportExportItem> clicked;
    private boolean isSum;

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

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
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

    public List<ReportExportItem> getClicked() {
        return clicked;
    }

    public void setClicked(List<ReportExportItem> clicked) {
        this.clicked = clicked;
    }

    public boolean isSum() {
        return isSum;
    }

    public void setSum(boolean sum) {
        isSum = sum;
    }
}
