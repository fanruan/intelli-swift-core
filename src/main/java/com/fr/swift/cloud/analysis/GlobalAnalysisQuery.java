package com.fr.swift.cloud.analysis;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.info.bean.element.filter.FilterInfoBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.queue.NPriorityQueue;
import com.fr.swift.util.Crasher;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lyon on 2018/12/20.
 */
public class GlobalAnalysisQuery implements MetricQuery {

    private static final int MILLI = 1000;
    private static final int MB = 1024 * 1024;

    private static String postfix = "Score";
    private static String resultTableName = "result";

    private static String T_NAME = "tName";
    private static String TOTAL = "total";
    private static String SQL_RATIO = "sqlRatio";
    private static String APP_ID = "appId";
    private static String YEAR_MONTH = "yearMonth";
    private static String FACTORS = "factors";

    private static String[] metrics = {
            "consume", "sqlTime", "coreConsume", "memory", "count"
    };
    private static String[] factors = new String[]{
            "conditionRatio", "sheetRatio", "dsRatio", "complexFormulaRatio", "imageSizeRatio", "sqlRatio"
    };

    private MetricQuery query;
    private MetricQuery tplQuery;

    private String customerId;
    private String yearMonth;

    public GlobalAnalysisQuery(FilterInfoBean tplFilter, FilterInfoBean metricFilter, String customerId, String yearMonth) {
        this.customerId = customerId;
        this.yearMonth = yearMonth;
        this.query = new GlobalTplMetricQuery(metricFilter);
        try {
            initTplQuery(tplFilter);
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
        List<List> sqlTime = new ArrayList<List>();
        List<List> core = new ArrayList<List>();
        List<List> memory = new ArrayList<List>();
        List<List> count = new ArrayList<List>();
        SwiftResultSet resultSet = query.getResult();
        if (!resultSet.hasNext()) {
            return new ArrayList<List>();
        }
        long id = 0;
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            long c = Math.round((Double) row.getValue(1) / MILLI);
            long s = Math.round((Double) row.getValue(2) / MILLI);
            long co = c - s;
            long m = Math.round((Double) row.getValue(3) / MB);
            long cc = Math.round((Double) row.getValue(4));
            base.add(toList(row.getValue(0), c, s, co, m, cc));
            consume.add(toList(id, c));
            sqlTime.add(toList(id, s));
            core.add(toList(id, co));
            memory.add(toList(id, m));
            count.add(toList(id, cc));
            id++;
        }

        addRank(consume, 1);
        addRank(sqlTime, 1);
        addRank(core, 1);
        addRank(memory, 1);
        addRank(count, 1);

        addGrade(base, new Grader.Time(), consume, 1);
        addGrade(base, new Grader.Time(), sqlTime, 1);
        addGrade(base, new Grader.Time(), core, 1);
        addGrade(base, new Grader.Memory(), memory, 1);
        addGrade(base, new Grader.Rank(count.size()), count, 2);

        long[] grades = new long[base.size()];
        for (int i = 0; i < base.size(); i++) {
            grades[getId(consume.get(i))] += Math.round((Long) consume.get(i).get(consume.get(i).size() - 1) * 0.8);
            grades[getId(count.get(i))] += Math.round((Long) count.get(i).get(count.get(i).size() - 1) * 0.2);
        }
        for (int i = 0; i < base.size(); i++) {
            base.get(i).add(grades[i]);
        }

        Collections.sort(base, new RowComparator(base.get(0).size() - 1));
        // 过滤出consume > 2的模板
        List<List> result = new ArrayList<List>();
        for (List row : base) {
            if ((Long) row.get(1) > Grader.Time.timeConsumeThreshold) {
                result.add(row);
            }
        }
        return result;
    }

    private static int getId(List row) {
        long id = (Long) row.get(0);
        return (int) id;
    }

    private static void addGrade(List<List> base, Grader grader, List<List> lists, int field) {
        for (List list : lists) {
            long id = (Long) list.get(0);
            long grade = grader.grade((Long) list.get(field));
            list.add(grade);
            base.get((int) id).add(grade);
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
        while (tplRS.hasNext()) {
            Row row = tplRS.getNextRow();
            String absPath = row.getValue(0);
            if (absPath == null) {
                continue;
            }
            List list = find(absPath, metrics);
            if (list != null) {
                list = new ArrayList(list);
                for (int i = 1; i < row.getSize(); i++) {
                    list.add(row.getValue(i));
                }
                // 增加一列sql时间导致性能问题的可能性
                list.add((Long) list.get(2) * 1.0 / (Long) list.get(1));
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
        StringBuilder builder = new StringBuilder();
        for (List row : result) {
            for (String field : factors) {
                Double value = (Double) row.get(resultFields.indexOf(field));
                queue.add(Pair.of(field, value));
            }
            Iterator<Pair<String, Double>> iterator = queue.toList().iterator();
            while (iterator.hasNext()) {
                String factor = iterator.next().getKey();
                builder.append(factor, 0, factor.indexOf("Ratio"));
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            row.add(builder.toString());
            builder.delete(0, builder.length());
        }
    }

    private SwiftMetaData createMetaData(List<SwiftMetaDataColumn> tplFields) {
        List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
        columns.add(new MetaDataColumnBean(T_NAME, Types.VARCHAR));
        for (String metric : metrics) {
            columns.add(new MetaDataColumnBean(metric, Types.BIGINT));
        }
        for (String metric : metrics) {
            columns.add(new MetaDataColumnBean(metric + postfix, Types.BIGINT));
        }
        columns.add(new MetaDataColumnBean(TOTAL, Types.BIGINT));
        // 模板属性
        columns.addAll(tplFields);
        columns.add(new MetaDataColumnBean(SQL_RATIO, Types.DOUBLE));
        columns.add(new MetaDataColumnBean(APP_ID, Types.VARCHAR));
        columns.add(new MetaDataColumnBean(YEAR_MONTH, Types.VARCHAR));
        columns.add(new MetaDataColumnBean(FACTORS, Types.VARCHAR));
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
