package com.fr.bi.cal.analyze.cal.result.operator;


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
    public void moveIterator(NodeDimensionIterator iterator) {
        iterator.moveToShrinkStartValue(clickValue);
    }

    @Override
    public Object[] getClickedValue() {
        return clickValue;
    }
}