package com.fr.swift.adaptor.log;

import com.fr.decision.log.LogSearchConstants;
import com.fr.decision.log.LogSearchProvider;
import com.fr.decision.log.MetricBean;
import com.fr.log.message.AbstractMessage;
import com.fr.stable.StringUtils;
import com.fr.stable.query.condition.QueryCondition;
import com.fr.stable.query.data.DataList;
import com.fr.swift.query.QueryConditionAdaptor;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.element.metric.Metric;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/6/21.
 */
public class SwiftLogSearchProvider implements LogSearchProvider {
    private static LogSearchProvider instance = new SwiftLogSearchProvider();

    public static LogSearchProvider getInstance() {
        return instance;
    }

    private SwiftLogSearchProvider() {
    }

    @Override
    public String getMarkString() {
        return LogSearchConstants.PROVIDER_MARK;
    }

    @Override
    public int count(Class<? extends AbstractMessage> logClass, QueryCondition condition) throws Exception {
        return countQuery(logClass, condition, "", null);
    }

    @Override
    public int countByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
        // TODO: 2018/6/21 这个和count表没什么区别
        return countQuery(logClass, condition, columnName, createNotNullFilter(columnName));
    }

    @Override
    public int distinctByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
        return countQuery(logClass, condition, columnName, createNotNullFilter(columnName));
    }

    @Override
    public List<Object> getValueByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add(columnName);
        List<Row> rows = LogQueryUtils.detailQuery(logClass, condition, fieldNames).getList();
        return IteratorUtils.iterator2List(new MapperIterator<Row, Object>(rows.iterator(), new Function<Row, Object>() {
            @Override
            public Object apply(Row p) {
                return p.getValue(0);
            }
        }));
    }

    @Override
    public List<Object> getDistinctValueByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add(columnName);
        List<Row> rows = LogQueryUtils.groupQuery(logClass, condition, fieldNames,
                new ArrayList<MetricBean>(), createNotNullFilter(columnName));
        return IteratorUtils.iterator2List(new MapperIterator<Row, Object>(rows.iterator(), new Function<Row, Object>() {
            @Override
            public Object apply(Row p) {
                return p.getValue(0);
            }
        }));
    }

    @Override
    public DataList<Map<String, Object>> groupByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, List<MetricBean> metrics, String columnName) throws Exception {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add(columnName);
        return groupByColumns(logClass, condition, metrics, fieldNames);
    }

    @Override
    public DataList<Map<String, Object>> groupByColumns(Class<? extends AbstractMessage> logClass, QueryCondition condition, List<MetricBean> metrics, List<String> fieldNames) throws Exception {
        final List<Row> rows = LogQueryUtils.groupQuery(logClass, condition, fieldNames, metrics, null);
        final List<String> columnNames = new ArrayList<String>(fieldNames);
        for (MetricBean bean : metrics) {
            columnNames.add(bean.getName());
        }
        List<Map<String, Object>> maps = IteratorUtils.iterator2List(new MapperIterator<Row, Map<String, Object>>(rows.iterator(), new Function<Row, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Row p) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < p.getSize(); i++) {
                    map.put(columnNames.get(i), p.getValue(i));
                }
                return map;
            }
        }));
        DataList<Map<String, Object>> dataList = new DataList<Map<String, Object>>();
        // TODO: 2018/6/21 这个totalCount不会是总行数吧？
        dataList.setTotalCount(maps.size());
        dataList.setList(maps);
        return dataList;
    }

    private static FilterInfoBean createNotNullFilter(String columnName) {
        if (StringUtils.isEmpty(columnName)) {
            return null;
        }
        NullFilterBean nullFilterBean = new NullFilterBean();
        nullFilterBean.setColumn(columnName);
        NotFilterBean notFilterBean = new NotFilterBean();
        notFilterBean.setFilterValue(nullFilterBean);
        return notFilterBean;
    }

    private static int countQuery(Class<? extends AbstractMessage> logClass, QueryCondition condition,
                                  String columnName, FilterInfoBean notNull) throws Exception {
        GroupQueryInfoBean queryInfoBean = new GroupQueryInfoBean();
        queryInfoBean.setQueryId(condition.toString());
        String tableName = SwiftMetaAdaptor.getTableName(logClass);
        queryInfoBean.setTableName(tableName);
        FilterInfoBean filterInfoBean = QueryConditionAdaptor.restriction2FilterInfo(condition.getRestriction());
        if (notNull != null) {
            AndFilterBean and = new AndFilterBean();
            and.setFilterValue(Arrays.asList(notNull, filterInfoBean));
            filterInfoBean = and;
        }
        queryInfoBean.setFilterInfoBean(filterInfoBean);

        List<com.fr.swift.query.info.bean.element.MetricBean> metrics = new ArrayList<com.fr.swift.query.info.bean.element.MetricBean>();
        com.fr.swift.query.info.bean.element.MetricBean bean = new com.fr.swift.query.info.bean.element.MetricBean();
        bean.setMetricType(Metric.MetricType.GROUP);
        if (StringUtils.isEmpty(columnName)) {
            bean.setType(AggregatorType.COUNT);
            bean.setColumn("");
        } else {
            bean.setType(AggregatorType.DISTINCT);
            bean.setColumn(columnName);
        }
        metrics.add(bean);
        queryInfoBean.setMetricBeans(metrics);
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().executeQuery(queryInfoBean);
        Row row = null;
        if (resultSet.next()) {
            row = resultSet.getNextRow();
        }
        if (row != null && row.getSize() == 1) {
            Number value = row.getValue(0);
            return value == null ? 0 : value.intValue();
        }
        //BI-25663 空的返回count0
        return 0;
    }
}
