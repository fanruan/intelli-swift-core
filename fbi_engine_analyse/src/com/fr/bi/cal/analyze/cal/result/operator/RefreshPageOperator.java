package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.sssecret.IRootDimensionGroup;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

public class RefreshPageOperator extends AbstractOperator {

    private Object[] clickValue;

    public RefreshPageOperator(int maxRow) {
        super(maxRow);
    }

    public RefreshPageOperator(Object[] clickValue, int maxRow) {
        super(maxRow);
        this.clickValue = clickValue;
    }

    @Override
    public NodeDimensionIterator[] getPageIterator(IRootDimensionGroup[] roots,
                                                   NodeExpander expander) {
        NodeDimensionIterator[] iters = new NodeDimensionIterator[roots.length];
        for (int i = 0; i < roots.length; i++) {
            roots[i].setExpander(expander);
            iters[i] = roots[i].moveToShrinkStartValue(clickValue);
        }
        return iters;
    }


}