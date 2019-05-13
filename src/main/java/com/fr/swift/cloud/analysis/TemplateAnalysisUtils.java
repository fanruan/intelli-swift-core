package com.fr.swift.cloud.analysis;

import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.ExecutionMetric;
import com.fr.swift.cloud.result.table.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.TemplateProperty;
import com.fr.swift.cloud.result.table.TemplatePropertyRatio;
import com.fr.swift.cloud.source.table.Execution;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.NumberInRangeFilterBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
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
    private static final int BATCH_SIZE = 20;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMM");

    public static void tplAnalysis(String appId, String yearMonth) throws Exception {
        // 为了避免重复，先清除数据
        try {
            deleteIfExisting(appId, yearMonth);
        } catch (Exception ignored) {
        }

        // 访问延时分位数统计图
        tpGraph(appId, yearMonth);

        // 性能问题模板过滤
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean(Execution.appId.getName(), appId),
                        new InFilterBean(Execution.yearMonth.getName(), yearMonth))
        );
        filter = tp90(filter);
        MetricQuery metricQuery = new GlobalAnalysisQuery(filter, appId, yearMonth);
        SwiftResultSet rs = metricQuery.getResult();
        Map<String, Integer> fieldIndexMap = fieldIndexMap(rs.getMetaData().getFieldNames());
        Date date = format.parse(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        Transaction transaction = session.beginTransaction();
        int count = 1;
        try {
            while (rs.hasNext()) {
                Row row = rs.getNextRow();
                String tName = row.<String>getValue(0);
                session.save(createExecutionMetric(tName, appId, date, row, fieldIndexMap));
                session.save(createTemplateProperty(tName, appId, date, row, fieldIndexMap));
                session.save(createTemplatePropertyRatio(tName, appId, date, row, fieldIndexMap));
                session.save(createTemplateAnalysisResult(appId, date, row, fieldIndexMap));
                if (count % BATCH_SIZE == 0) {
                    session.flush();
                    session.clear();
                }
                count++;
            }
        } finally {
            transaction.commit();
            try {
                session.close();
            } catch (Exception ignored) {
            }
            rs.close();
        }
        SwiftLoggers.getLogger().info("finished template analysis task with appId: {}, yearMonth: {}", appId, yearMonth);
    }

    private static TemplateAnalysisResult createTemplateAnalysisResult(String appId, Date yearMonth, Row row,
                                                                       Map<String, Integer> map) {
        String tName = row.getValue(0);
        return new TemplateAnalysisResult(
                tName,
                getLongValues("consume", "sqlTime", row, map),
                getLongValues("consumeMax", "sqlTimeMax", row, map),
                row.<Long>getValue(map.get("count")),
                row.<Long>getValue(map.get("total")),
                appId,
                yearMonth,
                getStringValues("factor1", "factor3", row, map)
        );
    }

    private static TemplatePropertyRatio createTemplatePropertyRatio(String tName, String appId, Date yearMonth, Row row,
                                                                     Map<String, Integer> map) {
        double[] values = getDoubleValues("conditionRatio", "sqlRatio", row, map);
        return new TemplatePropertyRatio(tName, values, appId, yearMonth);
    }

    private static TemplateProperty createTemplateProperty(String tName, String appId, Date yearMonth, Row row,
                                                           Map<String, Integer> map) {
        long[] values = getLongValues("condition", "imageSize", row, map);
        return new TemplateProperty(tName, values, appId, yearMonth);
    }

    private static ExecutionMetric createExecutionMetric(String tName, String appId, Date yearMonth, Row row, Map<String, Integer> map) {
        long[] values = getLongValues("consume", "count", row, map);
        return new ExecutionMetric(tName, values, appId, yearMonth);
    }

    private static String[] getStringValues(String start, String end, Row row, Map<String, Integer> map) {
        int s = map.get(start);
        int e = map.get(end);
        String[] values = new String[e - s + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = row.getValue(i + s);
        }
        return values;
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
            Double value = row.<Double>getValue(i + s);
            values[i] = value.isNaN() ? 0 : value;
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

    private static String[] tables = new String[]{
            ExecutionMetric.class.getSimpleName(),
            LatencyTopPercentileStatistic.class.getSimpleName(),
            TemplateAnalysisResult.class.getSimpleName(),
            TemplateProperty.class.getSimpleName(),
            TemplatePropertyRatio.class.getSimpleName()
    };

    private static void deleteIfExisting(String appId, String yearMonth) throws Exception {
        Date date = format.parse(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        for (String table : tables) {
            try {
                Transaction transaction = session.beginTransaction();
                Query query = session.createQuery(deleteSql(table));
                query.setParameter("appId", appId);
                query.setParameter("yearMonth", date);
                query.executeUpdate();
                transaction.commit();
            } catch (Exception ignored) {
            }
        }
        session.close();
    }

    private static String deleteSql(String tableName) {
        return "delete from " + tableName + " where appId = :appId and yearMonth = :yearMonth";
    }

    private static void tpGraph(String appId, String yearMonth) throws Exception {
        FilterInfoBean filter = new AndFilterBean(Arrays.<FilterInfoBean>asList(
                new InFilterBean(Execution.appId.getName(), appId),
                new InFilterBean(Execution.yearMonth.getName(), yearMonth)
        ));
        Date date = format.parse(yearMonth);
        Session session = ArchiveDBManager.INSTANCE.getFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            for (int percent = PERCENT_START; percent < PERCENT_END; percent += PERCENT_STEP) {
                long latency = tp(percent, filter);
                session.save(new LatencyTopPercentileStatistic(appId, date, percent, latency));
            }
        } finally {
            transaction.commit();
            try {
                session.close();
            } catch (Exception ignored) {
            }
        }
    }

    private static long tp(int percent, FilterInfoBean filter) throws Exception {
        GroupQueryInfoBean query = GroupQueryInfoBean.builder(Execution.tableName)
                .setFilter(filter)
                .setAggregations(MetricBean.builder(Execution.consume.getName(), AggregatorType.TOP_PERCENTILE)
                        .setParams(new Object[]{percent, 3}).build())
                .build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(query);
        Double value = null;
        try {
            value = resultSet.hasNext() ? resultSet.getNextRow().<Double>getValue(0) : null;
        } finally {
            resultSet.close();
        }
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
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(query);
        Double value = null;
        try {
            value = resultSet.hasNext() ? resultSet.getNextRow().<Double>getValue(0) : null;
        } finally {
            resultSet.close();
        }
        return new AndFilterBean(Arrays.asList(NumberInRangeFilterBean.builder(Execution.consume.getName())
                .setStart(Long.toString(value == null ? 0 : value.longValue()), true).build(), filter));
    }
}
