package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.ExpressionVisitor;
import com.fr.general.jsqlparser.statement.select.SelectItemVisitor;

/**
 * Created by pony on 2018/8/17.
 */
public interface DimensionMetricVisitor extends SelectItemVisitor,ExpressionVisitor {

}
