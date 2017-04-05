package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.node;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIBasicTableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kary on 2017/2/14.
 */
public class ReportNode {
    private List<BIBasicTableItem> children;
    private String dId;
    private String id;
    private String left;
    private String name;
    private ReportNode parent;
    private String right;

    public ReportNode() {
        initAttrs();
    }

    private void initAttrs() {
        children = new ArrayList<BIBasicTableItem>();
        dId = "";
        id = "";
        left = "";
        name = "";
        parent = null;
        right = "";
    }

    public ReportNode(String id) {
        this.id = id;
    }

    public List<BIBasicTableItem> getChildren() {
        return children;
    }

    public void setChildren(List<BIBasicTableItem> children) {
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

    public ReportNode getParent() {
        return parent;
    }

    public void setParent(ReportNode parent) {
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReportNode that = (ReportNode) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
