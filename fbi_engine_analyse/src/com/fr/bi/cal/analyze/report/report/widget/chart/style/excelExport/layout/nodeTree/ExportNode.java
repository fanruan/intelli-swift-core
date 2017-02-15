package com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.nodeTree;

import com.fr.bi.cal.analyze.report.report.widget.chart.style.excelExport.layout.table.ReportExportItem;

import java.util.List;

/**
 * Created by Kary on 2017/2/14.
 */
public class ExportNode {
    private List<ReportExportItem> children;
    private String dId;
    private String id;
    private String left;
    private String name;
    private ExportNode parent;
    private String right;

    public List<ReportExportItem> getChildren() {
        return children;
    }

    public void setChildren(List<ReportExportItem> children) {
        this.children = children;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExportNode getParent() {
        return parent;
    }

    public void setParent(ExportNode parent) {
        this.parent = parent;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportNode that = (ExportNode) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
