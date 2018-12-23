package com.fr.swift.jdbc.parser;

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
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.DetailFilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.OrFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.value.RangeFilterValueBean;
import com.fr.swift.util.Crasher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2018/8/20.
 */
public class FilterInfoBeanVisitor extends BaseExpressionVisitor implements FilterInfoBeanParser {
    private FilterInfoBean filterInfoBean;


    @Override
    public void visit(AndExpression andExpression) {
        filterInfoBean = new AndFilterBean();
        List<FilterInfoBean> filterInfoBeans = new ArrayList<FilterInfoBean>();
        FilterInfoBeanVisitor visitor = new FilterInfoBeanVisitor();
        andExpression.getLeftExpression().accept(visitor);
        filterInfoBeans.add(visitor.getFilterInfoBean());
        visitor = new FilterInfoBeanVisitor();
        andExpression.getRightExpression().accept(visitor);
        filterInfoBeans.add(visitor.getFilterInfoBean());
        filterInfoBean.setFilterValue(filterInfoBeans);
    }

    @Override
    public void visit(OrExpression orExpression) {
        filterInfoBean = new OrFilterBean();
        List<FilterInfoBean> filterInfoBeans = new ArrayList<FilterInfoBean>();
        FilterInfoBeanVisitor visitor = new FilterInfoBeanVisitor();
        orExpression.getLeftExpression().accept(visitor);
        filterInfoBeans.add(visitor.getFilterInfoBean());
        visitor = new FilterInfoBeanVisitor();
        orExpression.getRightExpression().accept(visitor);
        filterInfoBeans.add(visitor.getFilterInfoBean());
        filterInfoBean.setFilterValue(filterInfoBeans);
    }

    @Override
    public void visit(Between between) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(EqualsTo equalsTo) {
        filterInfoBean = new InFilterBean();
        final Set<String> filterValueBean = new HashSet<String>();
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.add(value);
            }
        });
        equalsTo.getLeftExpression().accept(visitor);
        equalsTo.getRightExpression().accept(visitor);
    }

    @Override
    public void visit(GreaterThan greaterThan) {
        filterInfoBean = new NumberInRangeFilterBean();
        final RangeFilterValueBean filterValueBean = new RangeFilterValueBean();
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.setStart(value);
            }
        });
        greaterThan.getLeftExpression().accept(visitor);
        greaterThan.getRightExpression().accept(visitor);
    }

    @Override
    public void visit(GreaterThanEquals greaterThanEquals) {
        filterInfoBean = new NumberInRangeFilterBean();
        final RangeFilterValueBean filterValueBean = new RangeFilterValueBean();
        filterValueBean.setStartIncluded(true);
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.setStart(value);
            }
        });
        greaterThanEquals.getLeftExpression().accept(visitor);
        greaterThanEquals.getRightExpression().accept(visitor);
    }

    @Override
    public void visit(InExpression inExpression) {
        filterInfoBean = new InFilterBean();
        final Set<String> filterValueBean = new HashSet<String>();
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.add(value);
            }
        });
        inExpression.getLeftExpression().accept(visitor);
        inExpression.getRightItemsList().accept(visitor);
    }

    @Override
    public void visit(IsNullExpression isNullExpression) {
        FilterInfoBean nullFilterBean = new NullFilterBean();
        if (isNullExpression.isNot()) {
            filterInfoBean = new NotFilterBean();
            filterInfoBean.setFilterValue(nullFilterBean);
        } else {
            filterInfoBean = nullFilterBean;
        }
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) nullFilterBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
            }
        });
        isNullExpression.getLeftExpression().accept(visitor);
    }

    @Override
    public void visit(LikeExpression likeExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(MinorThan minorThan) {
        filterInfoBean = new NumberInRangeFilterBean();
        final RangeFilterValueBean filterValueBean = new RangeFilterValueBean();
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.setEnd(value);
            }
        });
        minorThan.getLeftExpression().accept(visitor);
        minorThan.getRightExpression().accept(visitor);
    }

    @Override
    public void visit(MinorThanEquals minorThanEquals) {
        filterInfoBean = new NumberInRangeFilterBean();
        final RangeFilterValueBean filterValueBean = new RangeFilterValueBean();
        filterValueBean.setEndIncluded(true);
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.setEnd(value);
            }
        });
        minorThanEquals.getLeftExpression().accept(visitor);
        minorThanEquals.getRightExpression().accept(visitor);
    }

    @Override
    public void visit(NotEqualsTo notEqualsTo) {
        filterInfoBean = new NotFilterBean();
        FilterInfoBean inFilterBean = new InFilterBean();
        final Set<String> filterValueBean = new HashSet<String>();
        inFilterBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) inFilterBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.add(value);
            }
        });
        notEqualsTo.getLeftExpression().accept(visitor);
        notEqualsTo.getRightExpression().accept(visitor);
        filterInfoBean.setFilterValue(inFilterBean);
    }

    @Override
    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }
}
