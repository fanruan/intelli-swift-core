package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public interface Operator {

    void moveIterator(NodeDimensionIterator iterator);

    boolean isPageEnd();

    void addRow();

    int getCount();

    int getMaxRow();

    Object[] getClickedValue();


}