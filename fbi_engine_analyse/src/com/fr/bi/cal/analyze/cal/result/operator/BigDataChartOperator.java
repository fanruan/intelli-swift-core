package com.fr.bi.cal.analyze.cal.result.operator;

import com.fr.bi.cal.analyze.cal.sssecret.GroupConnectionValue;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;

/**
 * Created by andrew_asa on 2017/6/28.
 * 图表大数据分组限制操作器
 */
public class BigDataChartOperator implements Operator {

    /**
     * 图表展示时最大分组数
     */
    public static int MAXROW = 5000;

    /**
     * 当前行数
     */
    private int currentRow = 0;

    @Override
    public void moveIterator(NodeDimensionIterator iterator) {

    }

    @Override
    public boolean isPageEnd(GroupConnectionValue gc) {

        return currentRow > MAXROW;
    }

    @Override
    public void addRow() {

        currentRow++;
    }

    @Override
    public int getCount() {

        return 0;
    }

    @Override
    public int getMaxRow() {

        return MAXROW;
    }

    @Override
    public Object[] getClickedValue() {

        return new Object[0];
    }
}
