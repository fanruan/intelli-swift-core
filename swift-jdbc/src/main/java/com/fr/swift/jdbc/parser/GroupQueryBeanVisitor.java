package com.fr.swift.jdbc.parser;

import com.fr.general.jsqlparser.expression.Expression;
import com.fr.general.jsqlparser.expression.Function;
import com.fr.general.jsqlparser.schema.Column;
import com.fr.general.jsqlparser.statement.select.AllColumns;
import com.fr.general.jsqlparser.statement.select.AllTableColumns;
import com.fr.general.jsqlparser.statement.select.SelectExpressionItem;
import com.fr.swift.jdbc.exception.SwiftJDBCNotSupportedException;
import com.fr.swift.jdbc.exception.SwiftJDBCTableAbsentException;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/8/17.
 */
public class GroupQueryBeanVisitor extends AbstractQueryBeanVisitor {
    protected GroupQueryInfoBean queryBean;
    private static final Map<String, AggregatorType> function2AggType = new HashMap<String, AggregatorType>() {{
        put("sum", AggregatorType.SUM);
        put("count", AggregatorType.COUNT);
        put("min", AggregatorType.MIN);
        put("max", AggregatorType.MAX);
        put("avg", AggregatorType.AVERAGE);
    }};
    private SwiftMetaDataGetter metaDataGetter;

    public GroupQueryBeanVisitor(GroupQueryInfoBean queryBean, SwiftMetaDataGetter getter) {
        super(queryBean);
        this.queryBean = queryBean;
        this.metaDataGetter = getter;
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        Crasher.crash(new SwiftJDBCNotSupportedException());
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        selectExpressionItem.getExpression().accept(this);
    }

    @Override
    public void visit(AllColumns allColumns) {
        SwiftMetaData metaData = metaDataGetter.get();
        if (null == metaData) {
            Crasher.crash(new SwiftJDBCTableAbsentException(queryBean.getTableName()));
        }
        queryBean.setTableName(metaData.getId());
        try {
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                addColumn(metaData.getColumnName(i + 1));
            }
        } catch (SQLException e) {
            Crasher.crash(e);
        }
    }

    @Override
    public void visit(Function function) {
        List<Expression> expressions = function.getParameters().getExpressions();
        if (expressions.size() != 1 && !(expressions.get(0) instanceof Column)) {
            Crasher.crash(new SwiftJDBCNotSupportedException());
        }
        addColumn(((Column) expressions.get(0)).getColumnName(), getAggType(function.getName()));
    }

    private AggregatorType getAggType(String name) {
        if (function2AggType.containsKey(name)) {
            return function2AggType.get(name);
        }
        return AggregatorType.COUNT;
    }

    @Override
    protected void addColumn(String columnName) {
        columnName = QuoteUtils.trimQuote(columnName);
        addColumn(columnName, AggregatorType.COUNT);
    }

    protected void addColumn(String columnName, AggregatorType type) {
        List<MetricBean> metrics = queryBean.getMetricBeans();
        if (metrics == null) {
            metrics = new ArrayList<MetricBean>();
            queryBean.setMetricBeans(metrics);
        }
        MetricBean bean = new MetricBean();
        bean.setColumn(columnName);
        bean.setMetricType(MetricType.GROUP);
        bean.setType(type);
        metrics.add(bean);
    }
}
