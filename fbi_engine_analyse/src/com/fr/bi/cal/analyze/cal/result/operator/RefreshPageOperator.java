package com.fr.bi.cal.analyze.cal.result.operator;


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
    public NodeDimensionIterator getPageIterator(IRootDimensionGroup root) {
        return root.moveToShrinkStartValue(clickValue);
    }


}