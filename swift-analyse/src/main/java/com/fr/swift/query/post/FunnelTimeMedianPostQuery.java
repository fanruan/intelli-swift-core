package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.funnel.TimeWindowBean;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/13
 * TODO 2019/07/09 这个因为FunnelAggregatorValue调整这个也要变动，等Value写好之后再改
 * @author Lucifer
 * @description
 */
public class FunnelTimeMedianPostQuery implements Query<QueryResultSet<FunnelResultSet>> {

    private int timeWindow;
    private Query<FunnelQueryResultSet> postQuery;

    public FunnelTimeMedianPostQuery(Query<FunnelQueryResultSet> postQuery, TimeWindowBean timeWindowBean) {
        this.timeWindow = (int) timeWindowBean.toMillis();
        this.postQuery = postQuery;
    }


    private static double calMedian(List<Long> list, int[] helperArray) {
        Arrays.fill(helperArray, 0);
        for (long i : list) {
            helperArray[(int) i]++;
        }
        int half = list.size() / 2;
        int count = 0;
        int m = 0;
        for (int i = 0; i < helperArray.length; i++) {
            count += helperArray[i];
            if (count > half) {
                m = i;
                break;
            }
        }
        double median;
        if (list.size() % 2 == 0) {
            if (helperArray[m] > (count - half)) {
                median = (double) m;
            } else {
                int m1 = m;
                while (helperArray[--m1] == 0) {
                }
                median = ((double) m + (double) m1) / 2;
            }
        } else {
            median = (double) m;
        }
        return median;
    }

    @Override
    public QueryResultSet<FunnelResultSet> getQueryResult() throws SQLException {
        QueryResultSet<FunnelResultSet> resultSet = postQuery.getQueryResult();
        int[] helpArray = new int[timeWindow + 1];
        Map<FunnelGroupKey, FunnelAggValue> map = resultSet.getPage().getResult();
        for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : map.entrySet()) {
            List<List<Long>> periods = entry.getValue().getPeriods();
            double[] medians = new double[periods.size()];
            for (int i = 0; i < periods.size(); i++) {
                List<Long> list = periods.get(i);
                if (list.isEmpty()) {
                    medians[i] = Double.NaN;
                    continue;
                }
                medians[i] = calMedian(list, helpArray);
            }
            entry.getValue().setMedians(medians);
        }
        return resultSet;
    }
}
