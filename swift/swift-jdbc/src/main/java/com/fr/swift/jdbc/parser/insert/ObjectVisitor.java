package com.fr.swift.jdbc.parser.insert;

import com.fr.general.jsqlparser.expression.DateValue;
import com.fr.general.jsqlparser.expression.DoubleValue;
import com.fr.general.jsqlparser.expression.LongValue;
import com.fr.general.jsqlparser.expression.NullValue;
import com.fr.general.jsqlparser.expression.StringValue;
import com.fr.general.jsqlparser.expression.TimeValue;
import com.fr.general.jsqlparser.expression.TimestampValue;
import com.fr.swift.jdbc.parser.BaseExpressionVisitor;
import com.fr.swift.jdbc.parser.Getter;

/**
 * @author yee
 * @date 2018/8/27
 */
public class ObjectVisitor extends BaseExpressionVisitor implements Getter {

    private Object object;

    @Override
    public void visit(NullValue nullValue) {
        object = null;
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        object = doubleValue.getValue();
    }

    @Override
    public void visit(LongValue longValue) {
        object = longValue.getValue();
    }

    @Override
    public void visit(DateValue dateValue) {
        object = dateValue.getValue().getTime();
    }

    @Override
    public void visit(TimeValue timeValue) {
        object = timeValue.getValue().getTime();
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        object = timestampValue.getValue().getTime();
    }

    @Override
    public void visit(StringValue stringValue) {
        object = stringValue.getValue();
    }

    @Override
    public Object get() {
        return object;
    }
}
