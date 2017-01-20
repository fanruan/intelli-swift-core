package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public interface Operator {

    NodeDimensionIterator getPageIterator(IRootDimensionGroup root);

    boolean isPageEnd();

    void addRow();

    int getCount();


}