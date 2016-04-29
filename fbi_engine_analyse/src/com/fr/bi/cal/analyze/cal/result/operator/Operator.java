package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

import java.util.concurrent.ExecutorService;

public interface Operator {

    public NodeDimensionIterator[] getPageIterator(IRootDimensionGroup[] roots, NodeExpander expander);

    public boolean isPageEnd();

    public void addRow();

    public void setStartCount(int count);

    public int getCount();

    public ExecutorService get();

}