package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.Expression;
import com.fr.general.jsqlparser.expression.operators.conditional.AndExpression;
import com.fr.general.jsqlparser.expression.operators.conditional.OrExpression;
import com.fr.general.jsqlparser.expression.operators.relational.Between;
import com.fr.general.jsqlparser.expression.operators.relational.EqualsTo;
import com.fr.general.jsqlparser.expression.operators.relational.GreaterThan;
import com.fr.general.jsqlparser.expression.operators.relational.GreaterThanEquals;
import com.fr.general.jsqlparser.expression.operators.relational.InExpression;
import com.fr.general.jsqlparser.expression.operators.relational.IsNullExpression;
import com.fr.general.jsqlparser.expression.operators.relational.LikeExpression;
import com.fr.general.jsqlparser.expression.operators.relational.MinorThan;
import com.fr.general.jsqlparser.expression.operators.relational.MinorThanEquals;
import com.fr.general.jsqlparser.expression.operators.relational.NotEqualsTo;
import com.fr.general.jsqlparser.schema.Column;
import com.fr.swift.query.info.bean.query.AbstractSingleTableQueryInfoBean;

/**
 * Created by pony on 2018/8/20.
 */
public abstract class AbstractQueryBeanVisitor extends BaseExpressionVisitor implements DimensionMetricVisitor {
    private AbstractSingleTableQueryInfoBean queryInfoBean;

    public AbstractQueryBeanVisitor(AbstractSingleTableQueryInfoBean queryInfoBean) {
        this.queryInfoBean = queryInfoBean;
    }

    private void addFilterInfo(Expression expression) {
        FilterInfoBeanVisitor visitor = new FilterInfoBeanVisitor();
        expression.accept(visitor);
        queryInfoBean.setFilterInfoBean(visitor.getFilterInfoBean());
    }

    @Override
    public void visit(AndExpression andExpression) {
        addFilterInfo(andExpression);
    }

    @Override
    public void visit(OrExpression orExpression) {
        addFilterInfo(orExpression);
    }

    @Override
    public void visit(Between between) {
        addFilterInfo(between);
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        addFilterInfo(equalsTo);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        addFilterInfo(greaterThan);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        addFilterInfo(greaterThanEquals);
    }

    @Override
    public void visit(InExpression inExpression) {
        addFilterInfo(inExpression);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        addFilterInfo(isNullExpression);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        addFilterInfo(likeExpression);
    }

    @Override
    public void visit(MinorThan minorThan) {
        addFilterInfo(minorThan);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        addFilterInfo(minorThanEquals);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        addFilterInfo(notEqualsTo);
    }

    @Override
    public void visit(Column column) {
        addColumn(column.getColumnName());
    }

    protected abstract void addColumn(String columnName);

}
