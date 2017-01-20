package com.fr.bi.cal.analyze.cal.sssecret;


public class PageIteratorGroup {

    private IRootDimensionGroup rowRoot;

    private IRootDimensionGroup columnRoot;

    public PageIteratorGroup(){

    }

    public IRootDimensionGroup getRowRoot() {
        return rowRoot;
    }

    public IRootDimensionGroup getColumnRoot() {
        return columnRoot;
    }

    public void setRowRoot(IRootDimensionGroup rowRoot) {
        this.rowRoot = rowRoot;
    }

    public void setColumnRoot(IRootDimensionGroup columnRoot) {
        this.columnRoot = columnRoot;
    }
}