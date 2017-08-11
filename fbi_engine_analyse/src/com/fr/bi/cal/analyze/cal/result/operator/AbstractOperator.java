package com.fr.bi.cal.analyze.cal.result.operator;


import com.fr.bi.cal.analyze.cal.sssecret.GroupConnectionValue;
import com.fr.general.ComparatorUtils;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        AbstractOperator that = (AbstractOperator) o;

        if (maxRow != that.maxRow) {
            return false;
        }
        return equalsClickedValue(getClickedValue(), that.getClickedValue());
    }

    private boolean equalsClickedValue(Object[] clickedValue, Object[] thatValue){
        if (clickedValue == null){
            clickedValue = new Object[0];
        }
        if (thatValue == null){
            thatValue = new Object[0];
        }
        return ComparatorUtils.equals(clickedValue, thatValue);
    }

    @Override
    public int hashCode() {
        return maxRow;
    }
}