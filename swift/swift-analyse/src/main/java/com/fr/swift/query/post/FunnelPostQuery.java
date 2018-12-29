package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.query.info.bean.post.PostQueryInfoBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.type.PostQueryType;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelPostQuery implements PostQuery<QueryResultSet> {

    private boolean calMedian;
    private int timeWindow;
    private PostQuery postQuery;

    public FunnelPostQuery(PostQuery<QueryResultSet> postQuery, FunnelQueryBean queryBean) {
        this.calMedian = isCalMedian(queryBean);
        this.timeWindow = queryBean.getAggregation().getTimeWindow();
        this.postQuery = postQuery;
    }

    private boolean isCalMedian(FunnelQueryBean queryBean) {
        List<PostQueryInfoBean> beans = queryBean.getPostAggregations();
        for (PostQueryInfoBean bean : beans) {
            if (bean.getType() == PostQueryType.FUNNEL_MEDIAN) {
                return true;
            }
        }
        return false;
    }

    @Override
    public QueryResultSet getQueryResult() throws SQLException {
        FunnelQueryResultSet resultSet = (FunnelQueryResultSet) postQuery.getQueryResult();
        if (calMedian) {
            int[] helpArray = new int[timeWindow + 1];
            Map<FunnelGroupKey, FunnelAggValue> map = resultSet.getPage().getResult();
            for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : map.entrySet()) {
                List<List<Integer>> periods = entry.getValue().getPeriods();
                double[] medians = new double[periods.size()];
                for (int i = 0; i < periods.size(); i++) {
                    List<Integer> list = periods.get(i);
                    if (list.isEmpty()) {
                        medians[i] = Double.NaN;
                        continue;
                    }
                    medians[i] = calMedian(list, helpArray);
                }
                entry.getValue().setMedians(medians);
            }
        }
        return resultSet;
    }

    private static double calMedian(List<Integer> list, int[] helperArray) {
        Arrays.fill(helperArray, 0);
        for (int i : list) {
            helperArray[i]++;
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
                while (helperArray[--m1] == 0) ;
                median = ((double) m + (double) m1) / 2;
            }
        } else {
            median = (double) m;
        }
        return median;
    }
}
