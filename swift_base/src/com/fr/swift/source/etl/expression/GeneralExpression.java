package com.fr.swift.source.etl.expression;

import com.finebi.conf.constant.BIConfConstants;
import com.fr.swift.segment.Segment;

/**
 * Created by Handsome on 2018/3/1 0001 15:58
 */
public class GeneralExpression implements Expression {

    private Expression[] expressions;
    private LeftExpression leftValues;
    private int filterCount;
    private int columnType;
    private Object otherValue;
    private boolean isUnitLeft;


    public GeneralExpression(int filterCount, int columnType, Object otherValue, boolean isUnitLeft) {
        this.filterCount = filterCount;
        this.columnType = columnType;
        this.otherValue = otherValue;
        this.isUnitLeft = isUnitLeft;
    }

    private void init() {
        leftValues = new LeftExpression(isUnitLeft, otherValue);
        expressions = new Expression[filterCount];
        for(int i = 0; i < filterCount; i++) {
            switch (columnType) {
                case BIConfConstants.CONF.COLUMN.DATE:
                   // expressions[i] = new
            }
        }
    }

    @Override
    public Object get(Segment segment, int row, int columnType) {
        return null;
    }
}
