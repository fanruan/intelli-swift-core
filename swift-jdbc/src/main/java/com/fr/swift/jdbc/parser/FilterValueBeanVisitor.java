package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.DoubleValue;
import com.fr.general.jsqlparser.expression.Expression;
import com.fr.general.jsqlparser.expression.LongValue;
import com.fr.general.jsqlparser.expression.StringValue;
import com.fr.general.jsqlparser.expression.operators.relational.ExpressionList;
import com.fr.general.jsqlparser.expression.operators.relational.ItemsListVisitor;
import com.fr.general.jsqlparser.expression.operators.relational.MultiExpressionList;
import com.fr.general.jsqlparser.schema.Column;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.util.Crasher;

/**
 * Created by pony on 2018/8/20.
 */
public class FilterValueBeanVisitor<T> extends BaseExpressionVisitor implements ItemsListVisitor {

    // 目前要求写过滤条件的时候要求字段名写在左子表达式
    private boolean columnName = true;
    private DetailFilterInfoBean filterInfoBean;
    private FilterValueSetter<T> setter;

    public FilterValueBeanVisitor(DetailFilterInfoBean filterInfoBean, FilterValueSetter<T> setter) {
        this.filterInfoBean = filterInfoBean;
        this.setter = setter;
    }


    @Override
    public void visit(DoubleValue doubleValue) {
        setter.setValue((T) String.valueOf(doubleValue.getValue()));
    }

    @Override
    public void visit(LongValue longValue) {
        setter.setValue((T) longValue.getStringValue());
    }

    @Override
    public void visit(StringValue stringValue) {
        setter.setValue((T)stringValue.getValue());
    }


    @Override
    public void visit(Column column) {
        T value = (T) QuoteUtils.trimQuote(column.getColumnName());
        if (columnName) {
            filterInfoBean.setColumn((String) value);
            columnName = false;
        } else {
            setter.setValue(value);
        }
    }

    @Override
    public void visit(ExpressionList expressionList) {
        for (Expression expression : expressionList.getExpressions()){
            expression.accept(this);
        }
    }

    @Override
    public void visit(MultiExpressionList multiExpressionList) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }
}
