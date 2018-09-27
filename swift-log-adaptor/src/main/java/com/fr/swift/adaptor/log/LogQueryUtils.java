package com.fr.swift.adaptor.log;

import com.fr.decision.log.LogSearchConstants;
import com.fr.decision.log.MetricBean;
import com.fr.stable.StringUtils;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.GroupBean;
import com.fr.swift.query.info.bean.element.SortBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.post.RowSortQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.info.bean.type.MetricType;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.util.JpaAdaptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Lyon on 2018/6/21.
 */
public class LogQueryUtils {

    static QueryBean groupQuery(Class<?> entity, QueryCondition queryCondition, List<String> fieldNames,
                                List<MetricBean> metricBeans, FilterInfoBean notNullFilter) throws Exception {
        GroupQueryInfoBean queryInfoBean = new GroupQueryInfoBean();
        queryInfoBean.setQueryId(UUID.randomUUID().toString());
        String tableName = JpaAdaptor.getTableName(entity);
        queryInfoBean.setTableName(tableName);
        FilterInfoBean filterInfoBean = QueryConditionAdaptor.restriction2FilterInfo(queryCondition.getRestriction());
        if (notNullFilter != null) {
            AndFilterBean and = new AndFilterBean();
            and.setFilterValue(Arrays.asList(notNullFilter, filterInfoBean));
            filterInfoBean = and;
        }
        queryInfoBean.setFilterInfoBean(filterInfoBean);


        List<DimensionBean> dimensions = new ArrayList<DimensionBean>();
        for (int i = 0; i < fieldNames.size(); i++) {
            // TODO: 2018/6/21 维度上的排序没适配
            DimensionBean bean = new DimensionBean();
            bean.setColumn(fieldNames.get(i));
            bean.setDimensionType(DimensionType.GROUP);
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
            bean.setMetricType(MetricType.GROUP);
            bean.setColumn(metricBean.getFiledName());
            bean.setName(metricBean.getName());
            bean.setType(getAggType(metricBean));
            bean.setFilterInfoBean(createMetricFilterInfo(metricBean.getFiledName(), metricBean.getFiledFilter()));
            metrics.add(bean);
        }
        queryInfoBean.setMetricBeans(metrics);

        List<SortBean> sortBeans = new ArrayList<SortBean>();
        for (MetricBean metricBean : metricBeans) {
            if (StringUtils.equals(metricBean.getAsc(), LogSearchConstants.SORT_ASC)) {
                SortBean sortBean = new SortBean();
                // TODO: 2018/7/4 因为这边是结果排序，所以用字段转义名
                sortBean.setColumn(metricBean.getName());
                sortBean.setType(SortType.ASC);
                sortBeans.add(sortBean);
            }
            if (StringUtils.equals(metricBean.getAsc(), LogSearchConstants.SORT_DESC)) {
                SortBean sortBean = new SortBean();
                sortBean.setColumn(metricBean.getName());
                sortBean.setType(SortType.DESC);
                sortBeans.add(sortBean);
            }
        }
        List<PostQueryInfoBean> postQueryInfoBeans = new ArrayList<PostQueryInfoBean>();
        RowSortQueryInfoBean sortQueryInfoBean = new RowSortQueryInfoBean();
        sortQueryInfoBean.setSortBeans(sortBeans);
        postQueryInfoBeans.add(sortQueryInfoBean);
        queryInfoBean.setPostQueryInfoBeans(postQueryInfoBeans);

        return queryInfoBean;
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

    static QueryBean detailQuery(Class<?> entity, QueryCondition queryCondition, List<String> fieldNames) throws Exception {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(JpaAdaptor.getTableName(entity)));
        QueryBean queryBean = QueryConditionAdaptor.adaptCondition(queryCondition, table, fieldNames);
        return queryBean;
    }

    static List<Row> getPage(SwiftResultSet resultSet, QueryCondition queryCondition) throws SQLException {
        long start = queryCondition.getSkip();
        long end = queryCondition.getSkip() + queryCondition.getCount();
        //是否需要分页
        boolean isLimit = queryCondition.isCountLimitValid();
        List<Row> rows = new ArrayList<Row>();
        if (start >= end || !isLimit) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
        } else {
            long currentCount = 0;
            while (resultSet.hasNext() && currentCount < end) {
                if (currentCount++ < start) {
                    resultSet.getNextRow();
                    continue;
                }
                rows.add(resultSet.getNextRow());
            }
        }
        return rows;
    }

    static QueryBean getDetailQueryBean(Class<?> entity, QueryCondition queryCondition) throws SQLException {
        Table table = SwiftDatabase.getInstance().getTable(new SourceKey(JpaAdaptor.getTableName(entity)));
        QueryBean queryBean = QueryConditionAdaptor.adaptCondition(queryCondition, table, table.getMeta().getFieldNames());
        return queryBean;
    }
}
