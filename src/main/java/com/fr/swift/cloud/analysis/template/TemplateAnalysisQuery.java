package com.fr.swift.cloud.analysis.template;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.cloud.analysis.CloudQuery;
import com.fr.swift.cloud.analysis.ICloudQuery;
import com.fr.swift.cloud.result.ArchiveDBManager;
import com.fr.swift.cloud.result.table.template.ExecutionMetric;
import com.fr.swift.cloud.result.table.template.LatencyTopPercentileStatistic;
import com.fr.swift.cloud.result.table.template.TemplateAnalysisResult;
import com.fr.swift.cloud.result.table.template.TemplateProperty;
import com.fr.swift.cloud.result.table.template.TemplatePropertyRatio;
import com.fr.swift.cloud.util.TimeUtils;
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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lyon on 2019/3/1.
 */
@SwiftBean
@CloudQuery(name = "templateAnalysisQuery", tables = {"execution_metric", "latency_top_percentile_statistic", "template_analysis_result", "template_property", "template_property_ratio"})
public class TemplateAnalysisQuery implements ICloudQuery {

    private static final int PERCENT_START = 50;
    private static final int PERCENT_END = 100;
    private static final int PERCENT_STEP = 5;
    private static final int BATCH_SIZE = 20;

    @Override
    public void queryAndSave(String appId, String yearMonth) throws Exception {
        SwiftLoggers.getLogger().info("Start template analysis task with appId: {}, yearMonth: {}", appId, yearMonth);

        // 访问延时分位数统计图
        tpGraph(appId, yearMonth);

        // 性能问题模板过滤
        FilterInfoBean filter = new AndFilterBean(
                Arrays.<FilterInfoBean>asList(
                        new InFilterBean("appId", appId),
                        new InFilterBean("yearMonth", yearMonth))
        );
        filter = tp90(filter);
        MetricQuery metricQuery = new GlobalAnalysisQuery(filter, appId, yearMonth);
        SwiftResultSet rs = metricQuery.getResult();
        Map<String, Integer> fieldIndexMap = fieldIndexMap(rs.getMetaData().getFieldNames());
        Date date = TimeUtils.yearMonth2Date(yearMonth);
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
                session.save(createTemplateAnalysisResult(tName, appId, date, row, fieldIndexMap));
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

    private TemplateAnalysisResult createTemplateAnalysisResult(String tName, String appId, Date yearMonth, Row row,
                                                                Map<String, Integer> map) {
        return new TemplateAnalysisResult(tName, appId, yearMonth, row, map);
    }

    private TemplatePropertyRatio createTemplatePropertyRatio(String tName, String appId, Date yearMonth, Row row,
                                                              Map<String, Integer> map) {
        return new TemplatePropertyRatio(tName, row, map, appId, yearMonth);
    }

    private TemplateProperty createTemplateProperty(String tName, String appId, Date yearMonth, Row row,
                                                    Map<String, Integer> map) {
        return new TemplateProperty(tName, row, map, appId, yearMonth);
    }

    private ExecutionMetric createExecutionMetric(String tName, String appId, Date yearMonth, Row row, Map<String, Integer> map) {
        return new ExecutionMetric(tName, row, map, appId, yearMonth);
    }

    private Map<String, Integer> fieldIndexMap(List<String> fields) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < fields.size(); i++) {
            map.put(fields.get(i), i);
        }
        return map;
    }

    private void tpGraph(String appId, String yearMonth) throws Exception {
        FilterInfoBean filter = new AndFilterBean(Arrays.<FilterInfoBean>asList(
                new InFilterBean("appId", appId),
                new InFilterBean("yearMonth", yearMonth)
        ));
        Date date = TimeUtils.yearMonth2Date(yearMonth);
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

    private long tp(int percent, FilterInfoBean filter) throws Exception {
        GroupQueryInfoBean query = GroupQueryInfoBean.builder("execution")
                .setFilter(filter)
                .setAggregations(MetricBean.builder("consume", AggregatorType.TOP_PERCENTILE)
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

    private FilterInfoBean tp90(FilterInfoBean filter) throws Exception {
        GroupQueryInfoBean query = new GroupQueryInfoBean();
        query.setQueryId(UUID.randomUUID().toString());
        query.setTableName("execution");
        MetricBean metricBean = new MetricBean();
        metricBean.setType(AggregatorType.TOP_PERCENTILE);
        metricBean.setColumn("consume");
        metricBean.setParams(new Object[]{90, 3});
        query.setAggregations(Arrays.asList(metricBean));
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(query);
        Double value = null;
        try {
            value = resultSet.hasNext() ? resultSet.getNextRow().<Double>getValue(0) : null;
        } finally {
            resultSet.close();
        }
        return new AndFilterBean(Arrays.asList(NumberInRangeFilterBean.builder("consume")
                .setStart(Long.toString(value == null ? 0 : value.longValue()), true).build(), filter));
    }
}
