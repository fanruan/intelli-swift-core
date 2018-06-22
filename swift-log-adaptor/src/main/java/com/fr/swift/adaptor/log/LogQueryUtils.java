package com.fr.swift.adaptor.log;

import com.fr.decision.log.LogSearchConstants;
import com.fr.decision.log.MetricBean;
import com.fr.stable.StringUtils;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.aggregator.AggregatorFactory;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.group.Groups;
import com.fr.swift.query.group.impl.NoGroupRule;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.GroupDimension;
import com.fr.swift.query.info.element.metric.GroupMetric;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.GroupQueryInfoImpl;
import com.fr.swift.query.info.group.post.PostQueryInfo;
import com.fr.swift.query.query.QueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/6/21.
 */
public class LogQueryUtils {

    static List<Row> groupQuery(Class<?> entity, QueryCondition queryCondition, List<String> fieldNames,
                                List<MetricBean> metricBeans) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        FilterInfo filterInfo = QueryConditionAdaptor.restriction2FilterInfo(queryCondition.getRestriction());
        SourceKey sourceKey = table.getSourceKey();
        List<Dimension> dimensions = new ArrayList<Dimension>();
        for (int i = 0; i < fieldNames.size(); i++) {
            // TODO: 2018/6/21 维度上的排序没适配
            dimensions.add(new GroupDimension(i, sourceKey, new ColumnKey(fieldNames.get(i)),
                    Groups.newGroup(new NoGroupRule()), null, null));
        }
        List<Metric> metrics = new ArrayList<Metric>();
        for (int i = 0; i < metricBeans.size(); i++) {
            MetricBean metricBean = metricBeans.get(i);
            metrics.add(new GroupMetric(i, sourceKey, new ColumnKey(metricBean.getFiledName()),
                    createMetricFilterInfo(metricBean.getFiledName(), metricBean.getFiledFilter()), createMetric(metricBeans.get(i))));
        }
        GroupQueryInfo queryInfo = new GroupQueryInfoImpl("", sourceKey, filterInfo, dimensions, metrics, new ArrayList<PostQueryInfo>());
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        return getPage(resultSet, queryCondition);
    }

    private static FilterInfo createMetricFilterInfo(String fieldName, List<Object> fieldValues) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return null;
        }
        if (fieldValues.get(0) instanceof Integer) {
            Set<Integer> set = new HashSet<Integer>();
            for (Object object : fieldValues) {
                set.add((Integer) object);
            }
            return new SwiftDetailFilterInfo<Set<Integer>>(new ColumnKey(fieldName), set, SwiftDetailFilterType.NUMBER_CONTAIN);
        } else {
            Set<String> set = new HashSet<String>();
            for (Object object : fieldValues) {
                set.add((String) object);
            }
            return new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(fieldName), set, SwiftDetailFilterType.STRING_IN);
        }
    }

    private static Aggregator createMetric(MetricBean metricBean) {
        String type = metricBean.getType();
        if (StringUtils.equals(type, LogSearchConstants.AVERAGE)) {
            return AggregatorFactory.createAggregator(AggregatorType.AVERAGE);
        }
        if (StringUtils.equals(type, LogSearchConstants.COUNT)) {
            return AggregatorFactory.createAggregator(AggregatorType.COUNT);
        }
        if (StringUtils.equals(type, LogSearchConstants.MAX)) {
            return AggregatorFactory.createAggregator(AggregatorType.MAX);
        }
        if (StringUtils.equals(type, LogSearchConstants.MIN)) {
            return AggregatorFactory.createAggregator(AggregatorType.MIN);
        }
        return AggregatorFactory.createAggregator(AggregatorType.SUM);
    }

    static List<Row> detailQuery(Class<?> entity, QueryCondition queryCondition, List<String> fieldNames) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        QueryInfo queryInfo = QueryConditionAdaptor.adaptCondition(queryCondition, table, fieldNames);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfo);
        return getPage(resultSet, queryCondition);
    }

    private static List<Row> getPage(SwiftResultSet resultSet, QueryCondition queryCondition) throws SQLException {
        long start = queryCondition.getSkip();
        long end = queryCondition.getSkip() + queryCondition.getCount();
        // TODO: 2018/6/15 这边分页要做个缓存，用class + queryCondition.toString()作为key
        //是否需要分页
        boolean isLimit = queryCondition.isCountLimitValid();
        List<Row> rows = new ArrayList<Row>();
        long currentCount = 0;
        while (resultSet.next() && currentCount < end) {
            if (isLimit && currentCount < start) {
                continue;
            }
            rows.add(resultSet.getRowData());
            currentCount++;
        }
        return rows;
    }

    static List<Row> detailQuery(Class<?> entity, QueryCondition queryCondition) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        return detailQuery(entity, queryCondition, table.getMeta().getFieldNames());
    }
}
