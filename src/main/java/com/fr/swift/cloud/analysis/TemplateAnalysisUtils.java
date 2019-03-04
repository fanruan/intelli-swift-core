package com.fr.swift.cloud.analysis;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.ExecutionMetric;
import com.fr.swift.cloud.result.table.ExecutionMetricScore;
import com.fr.swift.cloud.result.table.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.TemplateProperty;
import com.fr.swift.cloud.result.table.TemplatePropertyRatio;
import com.fr.swift.cloud.source.table.Execution;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AllShowFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.Row;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lyon on 2019/3/1.
 */
public class TemplateAnalysisUtils {

    private static final int PERCENT_START = 50;
    private static final int PERCENT_END = 100;
    private static final int PERCENT_STEP = 5;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMM");


    public static void tplAnalysis(String appId, String yearMonth) throws Exception {
        // 访问延时分位数统计图
        tpGraph(appId, yearMonth);

        // 性能问题模板过滤
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean(Execution.appId.getName(), appId),
                        new InFilterBean(Execution.yearMonth.getName(), yearMonth))
        );
        filter = tp90(filter);
        MetricQuery metricQuery = new GlobalAnalysisQuery(new AllShowFilterBean(), filter, appId, yearMonth);
        SwiftResultSet rs = metricQuery.getResult();
        Map<String, Integer> fieldIndexMap = fieldIndexMap(rs.getMetaData().getFieldNames());
        Date date = format.parse(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            int count = 1;
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                session.save(createExecutionMetric(appId, date, row, fieldIndexMap));
                session.save(createExecutionMetricScore(appId, date, row, fieldIndexMap));
                session.save(createTemplateProperty(appId, date, row, fieldIndexMap));
                session.save(createTemplatePropertyRatio(appId, date, row, fieldIndexMap));
                session.save(createTemplateAnalysisResult(appId, date, row, fieldIndexMap));
                if (count % 10 == 0) {
                    transaction.commit();
                    transaction = session.beginTransaction();
                }
                count++;
            }
        } finally {
            transaction.commit();
            session.close();
            rs.close();
        }
    }

    private static TemplateAnalysisResult createTemplateAnalysisResult(String appId, Date yearMonth, Row row,
                                                                       Map<String, Integer> map) {
        String tName = row.getValue(0);
        return new TemplateAnalysisResult(
                tName,
                row.<Long>getValue(map.get("total")),
                appId,
                yearMonth,
                row.<String>getValue(map.get("factors"))
        );
    }

    private static TemplatePropertyRatio createTemplatePropertyRatio(String appId, Date yearMonth, Row row,
                                                                     Map<String, Integer> map) {
        String tName = row.getValue(0);
        double[] values = getDoubleValues("conditionRatio", "sqlRatio", row, map);
        return new TemplatePropertyRatio(tName, values, appId, yearMonth);
    }

    private static TemplateProperty createTemplateProperty(String appId, Date yearMonth, Row row,
                                                           Map<String, Integer> map) {
        String tName = row.getValue(0);
        long[] values = getLongValues("condition", "imageSize", row, map);
        return new TemplateProperty(tName, values, appId, yearMonth);
    }

    private static ExecutionMetricScore createExecutionMetricScore(String appId, Date yearMonth, Row row,
                                                                   Map<String, Integer> map) {
        String tName = row.getValue(0);
        long[] values = getLongValues("consumeScore", "countScore", row, map);
        return new ExecutionMetricScore(tName, values, appId, yearMonth);
    }

    private static ExecutionMetric createExecutionMetric(String appId, Date yearMonth, Row row, Map<String, Integer> map) {
        String tName = row.getValue(0);
        long[] values = getLongValues("consume", "count", row, map);
        return new ExecutionMetric(tName, values, appId, yearMonth);
    }

    private static long[] getLongValues(String start, String end, Row row, Map<String, Integer> map) {
        int s = map.get(start);
        int e = map.get(end);
        long[] values = new long[e - s + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = row.<Long>getValue(i + s);
        }
        return values;
    }

    private static double[] getDoubleValues(String start, String end, Row row, Map<String, Integer> map) {
        int s = map.get(start);
        int e = map.get(end);
        double[] values = new double[e - s + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = row.<Double>getValue(i + s);
        }
        return values;
    }

    private static Map<String, Integer> fieldIndexMap(List<String> fields) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < fields.size(); i++) {
            map.put(fields.get(i), i);
        }
        return map;
    }

    private static void tpGraph(String appId, String yearMonth) throws Exception {
        FilterInfoBean filter = new AndFilterBean(Arrays.<FilterInfoBean>asList(
                new InFilterBean(Execution.appId.getName(), appId),
                new InFilterBean(Execution.yearMonth.getName(), yearMonth)
        ));
        Date date = format.parse(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        try {
            Transaction transaction = session.beginTransaction();
            for (int percent = PERCENT_START; percent < PERCENT_END; percent += PERCENT_STEP) {
                long latency = tp(percent, filter);
                session.save(new LatencyTopPercentileStatistic(appId, date, percent, latency));
            }
            transaction.commit();
        } finally {
            session.close();
        }
    }

    private static long tp(int percent, FilterInfoBean filter) throws Exception {
        GroupQueryInfoBean query = GroupQueryInfoBean.builder(Execution.tableName)
                .setFilter(filter)
                .setAggregations(MetricBean.builder(Execution.consume.getName(), AggregatorType.TOP_PERCENTILE)
                        .setParams(new Object[]{percent, 3}).build())
                .build();
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(query)), query);
        Double value = resultSet.getNextRow().getValue(0);
        return value == null ? 0 : value.longValue();
    }

    private static FilterInfoBean tp90(FilterInfoBean filter) throws Exception {
        GroupQueryInfoBean query = new GroupQueryInfoBean();
        query.setQueryId(UUID.randomUUID().toString());
        query.setTableName(Execution.tableName);
        MetricBean metricBean = new MetricBean();
        metricBean.setType(AggregatorType.TOP_PERCENTILE);
        metricBean.setColumn(Execution.consume.getName());
        metricBean.setParams(new Object[]{90, 3});
        query.setAggregations(Arrays.asList(metricBean));
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(query)), query);
        Double lowerBound = resultSet.getNextRow().getValue(0);
        return new AndFilterBean(Arrays.asList(NumberInRangeFilterBean.builder(Execution.consume.getName())
                .setStart(Long.toString(lowerBound.longValue()), true).build(), filter));
    }
}
