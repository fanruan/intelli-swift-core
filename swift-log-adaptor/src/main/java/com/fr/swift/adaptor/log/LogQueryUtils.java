package com.fr.swift.adaptor.log;

import com.fr.decision.log.LogSearchConstants;
import com.fr.decision.log.MetricBean;
import com.fr.stable.StringUtils;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.DetailResultSet;
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
        GroupQueryInfoBean queryInfoBean = new GroupQueryInfoBean();
        queryInfoBean.setQueryId(queryCondition.toString());
        String tableName = SwiftMetaAdaptor.getTableName(entity);
        queryInfoBean.setTableName(tableName);
        FilterInfoBean filterInfoBean = QueryConditionAdaptor.restriction2FilterInfo(queryCondition.getRestriction());
        queryInfoBean.setFilterInfoBean(filterInfoBean);


        List<DimensionBean> dimensions = new ArrayList<DimensionBean>();
        for (int i = 0; i < fieldNames.size(); i++) {
            // TODO: 2018/6/21 维度上的排序没适配
            DimensionBean bean = new DimensionBean();
            bean.setTable(tableName);
            bean.setColumn(fieldNames.get(i));
            bean.setDimensionType(Dimension.DimensionType.GROUP);
            GroupBean groupBean = new GroupBean();
            groupBean.setType(GroupType.NONE);
            bean.setGroupBean(groupBean);
            dimensions.add(bean);
        }
        queryInfoBean.setDimensionBeans(dimensions);

        List<com.fr.swift.query.info.bean.element.MetricBean> metrics = new ArrayList<com.fr.swift.query.info.bean.element.MetricBean>();
        for (int i = 0; i < metricBeans.size(); i++) {
            MetricBean metricBean = metricBeans.get(i);
            com.fr.swift.query.info.bean.element.MetricBean bean = new com.fr.swift.query.info.bean.element.MetricBean();
            bean.setMetricType(Metric.MetricType.GROUP);
            bean.setColumn(metricBean.getFiledName());
            bean.setTable(tableName);
            bean.setType(getAggType(metricBean));
            bean.setFilterInfoBean(createMetricFilterInfo(metricBean.getFiledName(), metricBean.getFiledFilter()));
            metrics.add(bean);
        }
        queryInfoBean.setMetricBeans(metrics);

        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfoBean);
        return getPage(resultSet, queryCondition);
    }

    private static FilterInfoBean createMetricFilterInfo(String fieldName, List<Object> fieldValues) {
        if (fieldValues == null || fieldValues.isEmpty()) {
            return null;
        }
        Set<String> set = new HashSet<String>();
        for (Object object : fieldValues) {
            if (object == null) {
                continue;
            }
            set.add(object.toString());
        }
        InFilterBean in = new InFilterBean();
        in.setColumn(fieldName);
        in.setFilterValue(set);
        return in;
    }

    private static AggregatorType getAggType(MetricBean metricBean) {
        String type = metricBean.getType();
        if (StringUtils.equals(type, LogSearchConstants.AVERAGE)) {
            return AggregatorType.AVERAGE;
        }
        if (StringUtils.equals(type, LogSearchConstants.COUNT)) {
            return AggregatorType.COUNT;
        }
        if (StringUtils.equals(type, LogSearchConstants.MAX)) {
            return AggregatorType.MAX;
        }
        if (StringUtils.equals(type, LogSearchConstants.MIN)) {
            return AggregatorType.MIN;
        }
        return AggregatorType.SUM;
    }

    static DataList<Row> detailQuery(Class<?> entity, QueryCondition queryCondition, List<String> fieldNames) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        QueryBean queryBean = QueryConditionAdaptor.adaptCondition(queryCondition, table, fieldNames);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryBean);
        DataList<Row> dataList = new DataList<Row>();
        dataList.setList(getPage(resultSet, queryCondition));
        dataList.setTotalCount(((DetailResultSet) resultSet).getRowCount());
        return dataList;
    }

    private static List<Row> getPage(SwiftResultSet resultSet, QueryCondition queryCondition) throws SQLException {
        long start = queryCondition.getSkip();
        long end = queryCondition.getSkip() + queryCondition.getCount();
        // TODO: 2018/6/15 这边分页要做个缓存，用class + queryCondition.toString()作为key
        //是否需要分页
        boolean isLimit = queryCondition.isCountLimitValid();
        List<Row> rows = new ArrayList<Row>();
        if (start >= end || !isLimit) {
            while (resultSet.next()) {
                rows.add(resultSet.getRowData());
            }
        } else {
            long currentCount = 0;
            while (resultSet.next() && currentCount < end) {
                if (currentCount++ < start) {
                    resultSet.getRowData();
                    continue;
                }
                rows.add(resultSet.getRowData());
            }
        }
        return rows;
    }

    static DataList<Row> detailQuery(Class<?> entity, QueryCondition queryCondition) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(SwiftMetaAdaptor.getTableName(entity)));
        return detailQuery(entity, queryCondition, table.getMeta().getFieldNames());
    }
}
