package com.fr.swift.cloud.analysis.template;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.query.QueryRunnerProvider;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/12/31.
 */
public class TplPropertyQuery implements MetricQuery {

    private static String tableName = "tmp_property_proportion";
    private static String postfix = "Ratio";

    private GroupQueryInfoBean bean;
    private List<String> properties;

    {
        properties = new ArrayList<String>(Arrays.asList("condition", "formula", "sheet", "ds", "complexFormula", "submission", "frozen", "foldTree", "widget", "templateSize", "imageSize"));
    }

    public TplPropertyQuery(FilterInfoBean filter) {
        bean = GroupQueryInfoBean.builder("template_info")
                .setFilter(filter)
                .setDimensions(new DimensionBean(DimensionType.GROUP, "tName"))
                .setAggregations(createMetrics())
                .build();
    }

    private List<MetricBean> createMetrics() {
        List<MetricBean> metricBeans = new ArrayList<MetricBean>();
        for (String metric : properties) {
            MetricBean metricBean = new MetricBean();
            metricBean.setType(AggregatorType.MAX);
            metricBean.setColumn(metric);
            metricBeans.add(metricBean);
        }
        return metricBeans;
    }

    @Override
    public SwiftResultSet getResult() throws Exception {
        List<List> rows = new ArrayList<List>();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(QueryBeanFactory.queryBean2String(bean));
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            List list = new ArrayList();
            list.add(row.getValue(0));
            for (int i = 1; i < row.getSize(); i++) {
                if (row.getValue(i) != null) {
                    list.add(Math.round((Double) row.getValue(i)));
                } else {
                    list.add(0L);
                }
            }
            rows.add(list);
        }
        Grader grader = new Grader.Rank(rows.size());
        for (int i = 0; i < properties.size(); i++) {
            Collections.sort(rows, new GlobalAnalysisQuery.RowComparator(i + 1));
            for (int j = 0; j < rows.size(); j++) {
                addProportion(grader, rows.get(j), j + 1, properties.size());
            }
        }
        SwiftMetaData metaData = resultSet.getMetaData();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        for (String field : properties) {
            columns.add(metaData.getColumn(field));
        }
        for (int i = 0; i < properties.size(); i++) {
            columns.add(new MetaDataColumnBean(properties.get(i) + postfix, Types.DOUBLE));
        }
        final SwiftMetaData data = new SwiftMetaDataBean(tableName, columns);
        final Iterator<List> iterator = rows.listIterator();
        return createResultSet(data, iterator);
    }

    static SwiftResultSet createResultSet(final SwiftMetaData metaData, final Iterator<List> iterator) {
        return new SwiftResultSet() {
            @Override
            public int getFetchSize() {
                return 0;
            }

            @Override
            public SwiftMetaData getMetaData() throws SQLException {
                return metaData;
            }

            @Override
            public boolean hasNext() throws SQLException {
                return iterator.hasNext();
            }

            @Override
            public Row getNextRow() throws SQLException {
                return new ListBasedRow(iterator.next());
            }

            @Override
            public void close() throws SQLException {

            }
        };
    }

    private void addProportion(Grader grader, List row, long rank, int length) {
        if ((Long) row.get(row.size() - length) <= 0) {
            row.add(.0);
        } else {
            row.add(grader.grade(rank) / 100.0);
        }
    }
}
