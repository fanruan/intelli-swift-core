package com.fr.swift.cloud.analysis;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.query.info.bean.element.filter.impl.AndFilterBean;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.queue.NPriorityQueue;
import com.fr.swift.util.Crasher;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by lyon on 2018/12/20.
 */
public class GlobalAnalysisQuery implements MetricQuery {

    private static String resultTableName = "result";

    private static String T_NAME = "tName";
    private static String TOTAL = "total";
    private static String SQL_RATIO = "sqlRatio";
    private static String APP_ID = "appId";
    private static String YEAR_MONTH = "yearMonth";

    private static String[] metrics = {
            "consumeTop10", "consume", "coreConsume", "sqlTime", "count", "consumeMax", "coreConsumeMax", "sqlTimeMax"
    };

    private static String[] factors = new String[]{"factor1", "factor2", "factor3"};
    private static String[] ratios = new String[]{
            "conditionRatio", "sheetRatio", "dsRatio", "complexFormulaRatio", "imageSizeRatio", "sqlRatio"
    };

    private MetricQuery query;
    private MetricQuery tplQuery;

    private String customerId;
    private String yearMonth;

    public GlobalAnalysisQuery(FilterInfoBean tp10MetricFilter, String customerId, String yearMonth) {
        this.customerId = customerId;
        this.yearMonth = yearMonth;
        FilterInfoBean filter = new AndFilterBean(Arrays.<FilterInfoBean>asList(
                new InFilterBean("appId", customerId),
                new InFilterBean("yearMonth", yearMonth)
        ));
        this.query = new GlobalTplMetricQuery(filter, tp10MetricFilter);
        try {
            initTplQuery(filter);
        } catch (Exception e) {
            Crasher.crash(e);
        }
    }

    private void initTplQuery(FilterInfoBean filter) throws Exception {
        tplQuery = new TplPropertyQuery(filter);
    }

    private List<List> runModel() throws Exception {
        // fieldNames = [name, consume, sqlTime, memory]
        List<List> base = new ArrayList<List>();
        // 将指标拆分成3列，每列包括模板id
        List<List> consume = new ArrayList<List>();
        List<List> count = new ArrayList<List>();
        SwiftResultSet resultSet = query.getResult();
        if (!resultSet.hasNext()) {
            return new ArrayList<List>();
        }
        long id = 0;
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            Double value = row.getValue(1);
            long consumeTop10 = Math.round(value == null ? 0 : value.longValue());
            long consumeValue = Math.round((Double) row.getValue(2));
            long coreConsumeValue = Math.round((Double) row.getValue(3));
            long sqlTimeValue = Math.round((Double) row.getValue(4));
            long countValue = Math.round((Double) row.getValue(5));
            long consumeMax = Math.round((Double) row.getValue(6));
            long coreConsumeMax = Math.round((Double) row.getValue(7));
            long sqlTimeMax = Math.round((Double) row.getValue(8));
            base.add(toList(row.getValue(0), consumeTop10,
                    consumeValue, coreConsumeValue, sqlTimeValue,
                    countValue,
                    consumeMax, coreConsumeMax, sqlTimeMax));
            consume.add(toList(id, consumeTop10));
            count.add(toList(id, countValue));
            id++;
        }

        addRank(consume, 1);
        addRank(count, 1);

        addGrade(new Grader.Time(), consume, 1);
        addGrade(new Grader.Rank(count.size()), count, 2);

        long[] grades = new long[base.size()];
        for (int i = 0; i < base.size(); i++) {
            grades[getId(consume.get(i))] += Math.round((Long) consume.get(i).get(consume.get(i).size() - 1) * 0.8);
            grades[getId(count.get(i))] += Math.round((Long) count.get(i).get(count.get(i).size() - 1) * 0.2);
        }
        for (int i = 0; i < base.size(); i++) {
            base.get(i).add(grades[i]);
        }

        Collections.sort(base, new RowComparator(base.get(0).size() - 1));
        return base;
    }

    private static int getId(List row) {
        long id = (Long) row.get(0);
        return (int) id;
    }

    private static void addGrade(Grader grader, List<List> lists, int field) {
        for (List list : lists) {
            long grade = grader.grade((Long) list.get(field));
            list.add(grade);
        }
    }

    private static void addRank(List<List> lists, int field) {
        Collections.sort(lists, new RowComparator(field));
        for (long i = 0; i < lists.size(); i++) {
            lists.get((int) i).add(i + 1);
        }
    }

    private static List toList(Object... items) {
        List list = new ArrayList();
        for (Object object : items) {
            list.add(object);
        }
        return list;
    }

    @Override
    public SwiftResultSet getResult() throws Exception {
        List<List> metrics = runModel();
        SwiftResultSet tplRS = tplQuery.getResult();
        List<List> result = new ArrayList<List>();
        Set added = new HashSet();
        while (tplRS.hasNext()) {
            Row row = tplRS.getNextRow();
            String absPath = row.getValue(0);
            if (absPath == null) {
                continue;
            }
            List list = find(absPath, metrics);
            if (list != null) {
                if (added.contains(list.get(0))) {
                    continue;
                } else {
                    added.add(list.get(0));
                }
                list = new ArrayList(list);
                for (int i = 1; i < row.getSize(); i++) {
                    list.add(row.getValue(i));
                }
                // 增加一列sql时间导致性能问题的可能性
                Double sqlRatio = (Long) list.get(4) * 1.0 / (Long) list.get(2);
                sqlRatio = sqlRatio > .5 ? 1.0001 : sqlRatio;
                list.add(sqlRatio);
                list.add(customerId);
                list.add(yearMonth);
                result.add(list);
            }
        }
        SwiftMetaData tplRSMetaData = tplRS.getMetaData();
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        List<String> fields = tplRSMetaData.getFieldNames();
        for (String field : fields) {
            columns.add(tplRSMetaData.getColumn(field));
        }
        final SwiftMetaData data = createMetaData(columns);
        calculateFactors(data.getFieldNames(), result);
        final Iterator<List> iterator = result.iterator();
        return TplPropertyQuery.createResultSet(data, iterator);
    }

    private static void calculateFactors(List<String> resultFields, List<List> result) {
        NPriorityQueue<Pair<String, Double>> queue = new NPriorityQueue<Pair<String, Double>>(3, new Comparator<Pair<String, Double>>() {
            @Override
            public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
                double val1 = o1.getKey() == null ? -1 : o1.getValue();
                double val2 = o2.getKey() == null ? -1 : o2.getValue();
                return Double.compare(val1, val2);
            }
        });
        for (List row : result) {
            for (String field : ratios) {
                Double value = (Double) row.get(resultFields.indexOf(field));
                queue.add(Pair.of(field, value));
            }
            Iterator<Pair<String, Double>> iterator = queue.toList().iterator();
            while (iterator.hasNext()) {
                String factor = iterator.next().getKey();
                factor = factor.substring(0, factor.indexOf("Ratio"));
                row.add(factor);
            }
        }
    }

    private SwiftMetaData createMetaData(List<SwiftMetaDataColumn> tplFields) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        columns.add(new MetaDataColumnBean(T_NAME, Types.VARCHAR));
        for (String metric : metrics) {
            columns.add(new MetaDataColumnBean(metric, Types.BIGINT));
        }
        columns.add(new MetaDataColumnBean(TOTAL, Types.BIGINT));
        // 模板属性
        columns.addAll(tplFields);
        columns.add(new MetaDataColumnBean(SQL_RATIO, Types.DOUBLE));
        columns.add(new MetaDataColumnBean(APP_ID, Types.VARCHAR));
        columns.add(new MetaDataColumnBean(YEAR_MONTH, Types.VARCHAR));
        for (String factor : factors) {
            columns.add(new MetaDataColumnBean(factor, Types.VARCHAR));
        }
        return new SwiftMetaDataBean(resultTableName, columns);
    }

    private static List find(String absPath, List<List> metrics) {
        for (List row : metrics) {
            String path = (String) row.get(0);
            if (path == null) {
                continue;
            }
            if (absPath.endsWith(path)) {
                return row;
            }
        }
        return null;
    }

    static class RowComparator implements Comparator<List> {

        private int index;
        private Comparator<Long> comparator = Comparators.desc();

        public RowComparator(int index) {
            this.index = index;
        }

        @Override
        public int compare(List o1, List o2) {
            return comparator.compare((Long) o1.get(index), (Long) o2.get(index));
        }
    }
}
