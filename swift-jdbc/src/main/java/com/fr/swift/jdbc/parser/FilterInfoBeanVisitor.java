package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.AllComparisonExpression;
import com.fr.general.jsqlparser.expression.AnalyticExpression;
import com.fr.general.jsqlparser.expression.AnyComparisonExpression;
import com.fr.general.jsqlparser.expression.CaseExpression;
import com.fr.general.jsqlparser.expression.CastExpression;
import com.fr.general.jsqlparser.expression.DateTimeLiteralExpression;
import com.fr.general.jsqlparser.expression.DateValue;
import com.fr.general.jsqlparser.expression.DoubleValue;
import com.fr.general.jsqlparser.expression.ExpressionVisitor;
import com.fr.general.jsqlparser.expression.ExtractExpression;
import com.fr.general.jsqlparser.expression.Function;
import com.fr.general.jsqlparser.expression.HexValue;
import com.fr.general.jsqlparser.expression.IntervalExpression;
import com.fr.general.jsqlparser.expression.JdbcNamedParameter;
import com.fr.general.jsqlparser.expression.JdbcParameter;
import com.fr.general.jsqlparser.expression.JsonExpression;
import com.fr.general.jsqlparser.expression.KeepExpression;
import com.fr.general.jsqlparser.expression.LongValue;
import com.fr.general.jsqlparser.expression.MySQLGroupConcat;
import com.fr.general.jsqlparser.expression.NotExpression;
import com.fr.general.jsqlparser.expression.NullValue;
import com.fr.general.jsqlparser.expression.NumericBind;
import com.fr.general.jsqlparser.expression.OracleHierarchicalExpression;
import com.fr.general.jsqlparser.expression.OracleHint;
import com.fr.general.jsqlparser.expression.Parenthesis;
import com.fr.general.jsqlparser.expression.RowConstructor;
import com.fr.general.jsqlparser.expression.SignedExpression;
import com.fr.general.jsqlparser.expression.StringValue;
import com.fr.general.jsqlparser.expression.TimeKeyExpression;
import com.fr.general.jsqlparser.expression.TimeValue;
import com.fr.general.jsqlparser.expression.TimestampValue;
import com.fr.general.jsqlparser.expression.UserVariable;
import com.fr.general.jsqlparser.expression.WhenClause;
import com.fr.general.jsqlparser.expression.WithinGroupExpression;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Addition;
import com.fr.general.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import com.fr.general.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import com.fr.general.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Concat;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Division;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Modulo;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Multiplication;
import com.fr.general.jsqlparser.expression.operators.arithmetic.Subtraction;
import com.fr.general.jsqlparser.expression.operators.conditional.AndExpression;
import com.fr.general.jsqlparser.expression.operators.conditional.OrExpression;
import com.fr.general.jsqlparser.expression.operators.relational.Between;
import com.fr.general.jsqlparser.expression.operators.relational.EqualsTo;
import com.fr.general.jsqlparser.expression.operators.relational.ExistsExpression;
import com.fr.general.jsqlparser.expression.operators.relational.GreaterThan;
import com.fr.general.jsqlparser.expression.operators.relational.GreaterThanEquals;
import com.fr.general.jsqlparser.expression.operators.relational.InExpression;
import com.fr.general.jsqlparser.expression.operators.relational.IsNullExpression;
import com.fr.general.jsqlparser.expression.operators.relational.JsonOperator;
import com.fr.general.jsqlparser.expression.operators.relational.LikeExpression;
import com.fr.general.jsqlparser.expression.operators.relational.Matches;
import com.fr.general.jsqlparser.expression.operators.relational.MinorThan;
import com.fr.general.jsqlparser.expression.operators.relational.MinorThanEquals;
import com.fr.general.jsqlparser.expression.operators.relational.NotEqualsTo;
import com.fr.general.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import com.fr.general.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import com.fr.general.jsqlparser.schema.Column;
import com.fr.general.jsqlparser.statement.select.SubSelect;
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
public class FilterInfoBeanVisitor implements ExpressionVisitor, FilterInfoBeanParser {
    private FilterInfoBean filterInfoBean;

    @Override
    public void visit(NullValue nullValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Function function) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SignedExpression signedExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(JdbcParameter jdbcParameter) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(JdbcNamedParameter jdbcNamedParameter) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(DoubleValue doubleValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(LongValue longValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(HexValue hexValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(DateValue dateValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(TimeValue timeValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(TimestampValue timestampValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Parenthesis parenthesis) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(StringValue stringValue) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Addition addition) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Division division) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Multiplication multiplication) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Subtraction subtraction) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

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
        filterInfoBean = new NullFilterBean();
        final Set<String> filterValueBean = new HashSet<String>();
        filterInfoBean.setFilterValue(filterValueBean);
        FilterValueBeanVisitor<String> visitor = new FilterValueBeanVisitor<String>((DetailFilterInfoBean) filterInfoBean, new FilterValueSetter<String>() {
            @Override
            public void setValue(String value) {
                filterValueBean.add(value);
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
        filterValueBean.setEndIncluded(false);
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
    public void visit(Column column) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SubSelect subSelect) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(CaseExpression caseExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(WhenClause whenClause) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(ExistsExpression existsExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(AllComparisonExpression allComparisonExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(AnyComparisonExpression anyComparisonExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Concat concat) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Matches matches) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(BitwiseAnd bitwiseAnd) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(BitwiseOr bitwiseOr) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(BitwiseXor bitwiseXor) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(CastExpression castExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(Modulo modulo) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(AnalyticExpression analyticExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(WithinGroupExpression withinGroupExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(ExtractExpression extractExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(IntervalExpression intervalExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(OracleHierarchicalExpression oracleHierarchicalExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(RegExpMatchOperator regExpMatchOperator) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(JsonExpression jsonExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(JsonOperator jsonOperator) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(RegExpMySQLOperator regExpMySQLOperator) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(UserVariable userVariable) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(NumericBind numericBind) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(KeepExpression keepExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(MySQLGroupConcat mySQLGroupConcat) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(RowConstructor rowConstructor) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(OracleHint oracleHint) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(TimeKeyExpression timeKeyExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(DateTimeLiteralExpression dateTimeLiteralExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(NotExpression notExpression) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public FilterInfoBean getFilterInfoBean() {
        return filterInfoBean;
    }
}
