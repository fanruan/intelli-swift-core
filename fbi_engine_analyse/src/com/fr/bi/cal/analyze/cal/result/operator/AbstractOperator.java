package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.GroupConnectionValue;

public abstract class AbstractOperator implements Operator {

    private int counter = 0;

    private int maxRow = 20;

    AbstractOperator(int maxRow) {
        this.maxRow = maxRow;
    }

    @Override
    public int getCount() {
        return counter;
    }

    @Override
    public void addRow() {
        counter++;
    }

    @Override
    public boolean isPageEnd(GroupConnectionValue gc) {
        return counter >= maxRow;
    }

    @Override
    public int getMaxRow() {
        return maxRow;
    }

    @Override
    public Object[] getClickedValue() {
        return new Object[0];
    }
}