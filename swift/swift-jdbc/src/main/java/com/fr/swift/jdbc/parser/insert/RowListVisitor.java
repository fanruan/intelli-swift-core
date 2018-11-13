package com.fr.swift.jdbc.parser.insert;

import com.fr.general.jsqlparser.expression.Expression;
import com.fr.general.jsqlparser.expression.operators.relational.ExpressionList;
import com.fr.general.jsqlparser.expression.operators.relational.ItemsListVisitor;
import com.fr.general.jsqlparser.expression.operators.relational.MultiExpressionList;
import com.fr.general.jsqlparser.statement.select.SubSelect;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.parser.Getter;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/8/27
 */
public class RowListVisitor implements ItemsListVisitor, Getter<List<Row>> {

    private List<Row> data;

    public RowListVisitor() {
        data = new ArrayList<Row>();
    }

    @Override
    public void visit(SubSelect subSelect) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(ExpressionList expressionList) {
        List rowList = new ArrayList();
        for (Expression expression : expressionList.getExpressions()) {
            ObjectVisitor visitor = new ObjectVisitor();
            expression.accept(visitor);
            rowList.add(visitor.get());
        }
        data.add(new ListBasedRow(rowList));
    }

    @Override
    public void visit(MultiExpressionList multiExpressionList) {
        for (ExpressionList expressionList : multiExpressionList.getExprList()) {
            visit(expressionList);
        }
    }


    @Override
    public List<Row> get() {
        return data;
    }
}
