//package com.fr.swift.adaptor.log;
//
//import com.fr.decision.log.LogSearchConstants;
//import com.fr.decision.log.LogSearchProvider;
//import com.fr.decision.log.MetricBean;
//import com.fr.io.context.ResourceModuleContext;
//import com.fr.io.repository.FineFileEntry;
//import com.fr.io.utils.ResourceIOUtils;
//import com.fr.log.message.AbstractMessage;
//import com.fr.stable.StableUtils;
//import com.fr.stable.StringUtils;
//import com.fr.stable.query.condition.QueryCondition;
//import com.fr.stable.query.data.DataList;
//import com.fr.swift.basics.base.selector.ProxySelector;
//import com.fr.swift.db.SwiftDatabase;
//import com.fr.swift.query.aggregator.AggregatorType;
//import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
//import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
//import com.fr.swift.query.info.bean.element.filter.impl.NotFilterBean;
//import com.fr.swift.query.info.bean.element.filter.impl.NullFilterBean;
//import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
//import com.fr.swift.query.info.bean.type.MetricType;
//import com.fr.swift.query.query.QueryBean;
//import com.fr.swift.result.DetailResultSet;
//import com.fr.swift.service.AnalyseService;
//import com.fr.swift.source.Row;
//import com.fr.swift.result.SwiftResultSet;
//import com.fr.swift.structure.iterator.IteratorUtils;
//import com.fr.swift.structure.iterator.MapperIterator;
//import com.fr.swift.util.Crasher;
//import com.fr.swift.util.JpaAdaptor;
//import com.fr.swift.util.function.Function;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
///**
// * Created by Lyon on 2018/6/21.
// */
//public class SwiftLogSearchProvider implements LogSearchProvider {
//    private static LogSearchProvider instance = new SwiftLogSearchProvider();
//
//    public static LogSearchProvider getInstance() {
//        return instance;
//    }
//
//    private AnalyseService analyseService;
//
//    private SwiftLogSearchProvider() {
//        analyseService = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//    }
//
//    @Override
//    public String getMarkString() {
//        return LogSearchConstants.PROVIDER_MARK;
//    }
//
//    @Override
//    public int count(Class<? extends AbstractMessage> logClass, QueryCondition condition) throws Exception {
//        List<Field> fields = JpaAdaptor.getFields(logClass);
//        if (fields.isEmpty()) {
//            return Crasher.crash("Unsupported Operation: count without field name!");
//        }
//        return countQuery(logClass, condition, fields.get(0).getName(), null, true);
//    }
//
//    @Override
//    public int countByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
//        // TODO: 2018/6/21 这个和count表没什么区别
//        return countQuery(logClass, condition, columnName, createNotNullFilter(columnName), true);
//    }
//
//    @Override
//    public int distinctByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
//        return countQuery(logClass, condition, columnName, createNotNullFilter(columnName), false);
//    }
//
//    @Override
//    public List<Object> getValueByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
//        List<String> fieldNames = new ArrayList<String>();
//        fieldNames.add(columnName);
//        QueryBean queryBean = LogQueryUtils.detailQuery(logClass, condition, fieldNames);
//
//        SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
//        DataList<Row> dataList = new DataList<Row>();
//        dataList.setList(LogQueryUtils.getPage(resultSet, condition));
//        dataList.setTotalCount(((DetailResultSet) resultSet).getRowCount());
//        List<Row> rows = dataList.getList();
//        return IteratorUtils.iterator2List(new MapperIterator<Row, Object>(rows.iterator(), new Function<Row, Object>() {
//            @Override
//            public Object apply(Row p) {
//                return p.getValue(0);
//            }
//        }));
//    }
//
//    @Override
//    public List<Object> getDistinctValueByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, String columnName) throws Exception {
//        List<String> fieldNames = new ArrayList<String>();
//        fieldNames.add(columnName);
//        QueryBean queryBean = LogQueryUtils.groupQuery(logClass, condition, fieldNames,
//                new ArrayList<MetricBean>(), createNotNullFilter(columnName));
//        SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
//        List<Row> rows = LogQueryUtils.getPage(resultSet, condition);
//        return IteratorUtils.iterator2List(new MapperIterator<Row, Object>(rows.iterator(), new Function<Row, Object>() {
//            @Override
//            public Object apply(Row p) {
//                return p.getValue(0);
//            }
//        }));
//    }
//
//    @Override
//    public DataList<Map<String, Object>> groupByColumn(Class<? extends AbstractMessage> logClass, QueryCondition condition, List<MetricBean> metrics, String columnName) throws Exception {
//        List<String> fieldNames = new ArrayList<String>();
//        fieldNames.add(columnName);
//        return groupByColumns(logClass, condition, metrics, fieldNames);
//    }
//
//    @Override
//    public DataList<Map<String, Object>> groupByColumns(Class<? extends AbstractMessage> logClass, QueryCondition condition, List<MetricBean> metrics, List<String> fieldNames) throws Exception {
//        QueryBean queryBean = LogQueryUtils.groupQuery(logClass, condition, fieldNames, metrics, null);
//        SwiftResultSet resultSet = analyseService.getQueryResult(queryBean);
//        List<Row> rows = LogQueryUtils.getPage(resultSet, condition);
//        final List<String> columnNames = new ArrayList<String>(fieldNames);
//        for (MetricBean bean : metrics) {
//            columnNames.add(bean.getName());
//        }
//        List<Map<String, Object>> maps = IteratorUtils.iterator2List(new MapperIterator<Row, Map<String, Object>>(rows.iterator(), new Function<Row, Map<String, Object>>() {
//            @Override
//            public Map<String, Object> apply(Row p) {
//                Map<String, Object> map = new HashMap<String, Object>();
//                for (int i = 0; i < p.getSize(); i++) {
//                    map.put(columnNames.get(i), p.getValue(i));
//                }
//                return map;
//            }
//        }));
//        DataList<Map<String, Object>> dataList = new DataList<Map<String, Object>>();
//        dataList.setTotalCount(maps.size());
//        dataList.setList(maps);
//        return dataList;
//    }
//
//    private FilterInfoBean createNotNullFilter(String columnName) {
//        if (StringUtils.isEmpty(columnName)) {
//            return null;
//        }
//        NullFilterBean nullFilterBean = new NullFilterBean();
//        nullFilterBean.setColumn(columnName);
//        NotFilterBean notFilterBean = new NotFilterBean();
//        notFilterBean.setFilterValue(nullFilterBean);
//        return notFilterBean;
//    }
//
//    private int countQuery(Class<? extends AbstractMessage> logClass, QueryCondition condition,
//                           String columnName, FilterInfoBean notNull, boolean count) throws Exception {
//        GroupQueryInfoBean queryInfoBean = new GroupQueryInfoBean();
//        queryInfoBean.setQueryId(UUID.randomUUID().toString());
//        String tableName = JpaAdaptor.getTableName(logClass);
//        queryInfoBean.setTableName(tableName);
//        FilterInfoBean filterInfoBean = QueryConditionAdaptor.restriction2FilterInfo(condition.getRestriction());
//        if (notNull != null) {
//            AndFilterBean and = new AndFilterBean();
//            and.setFilterValue(Arrays.asList(notNull, filterInfoBean));
//            filterInfoBean = and;
//        }
//        queryInfoBean.setFilter(filterInfoBean);
//
//        List<com.fr.swift.query.info.bean.element.MetricBean> metrics = new ArrayList<com.fr.swift.query.info.bean.element.MetricBean>();
//        com.fr.swift.query.info.bean.element.MetricBean bean = new com.fr.swift.query.info.bean.element.MetricBean();
//        bean.setMetricType(MetricType.GROUP);
//        if (count) {
//            bean.setType(AggregatorType.COUNT);
//        } else {
//            bean.setType(AggregatorType.DISTINCT);
//        }
//        bean.setColumn(columnName);
//        metrics.add(bean);
//        queryInfoBean.setMetricBeans(metrics);
//        SwiftResultSet resultSet = analyseService.getQueryResult(queryInfoBean);
//        Row row = null;
//        if (resultSet.hasNext()) {
//            row = resultSet.getNextRow();
//        }
//        if (row != null && row.getSize() == 1) {
//            Number value = row.getValue(0);
//            return value == null ? 0 : value.intValue();
//        }
//        //BI-25663 空的返回count0
//        return 0;
//    }
//
//    public static long getSizeOf(String dir) {
//        long size = 0L;
//        FineFileEntry[] entries = ResourceIOUtils.listEntry(dir);
//        for (FineFileEntry entry : entries) {
//            if (entry.isDirectory()) {
//                size += getSizeOf(StableUtils.pathJoin(dir, entry.getName()));
//            } else {
//                size += entry.getSize();
//            }
//        }
//        return size;
//    }
//
//    public long logTotal() {
//        long size = 0;
//        if (ResourceModuleContext.getRealCurrentRepo().isAccurateDiskSize()) {
//            return getSizeOf("../" + SwiftDatabase.DECISION_LOG.getDir());
//        }
//        return size;
//    }
//}
