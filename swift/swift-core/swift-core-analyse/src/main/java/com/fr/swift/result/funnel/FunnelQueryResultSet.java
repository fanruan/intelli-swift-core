package com.fr.swift.result.funnel;

import com.fr.swift.query.aggregator.FunnelAggValue;
import com.fr.swift.query.group.FunnelGroupKey;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.RowSwiftResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelQueryResultSet implements QueryResultSet<FunnelResultSet>, Serializable {

    private static final long serialVersionUID = 5730624500314766659L;

    private FunnelResultSet funnelResultSet;
    private QueryResultSetMerger merger;

    public FunnelQueryResultSet(FunnelResultSet funnelResultSet, QueryResultSetMerger merger) {
        this.funnelResultSet = funnelResultSet;
        this.merger = merger;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public FunnelResultSet getPage() {
        return funnelResultSet;
    }

    @Override
    public boolean hasNextPage() {
        return false;
    }

    @Override
    public <Q extends QueryResultSet<FunnelResultSet>> QueryResultSetMerger<FunnelResultSet, Q> getMerger() {
        return merger;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        List<Row> rows = new ArrayList<Row>();
        Map<FunnelGroupKey, FunnelAggValue> map = funnelResultSet.getResult();
        for (Map.Entry<FunnelGroupKey, FunnelAggValue> entry : map.entrySet()) {
            List list = new ArrayList();
            FunnelGroupKey key = entry.getKey();
            list.add(key.getDate());
            FunnelGroupKey.GroupType type = key.getType();
            if (type == FunnelGroupKey.GroupType.RANGE) {
                List<Double> pair = key.getRangePair();
                list.add(pair.isEmpty() ? "" : pair.get(0) + "-" + pair.get(1));
            } else if (type == FunnelGroupKey.GroupType.NORMAL) {
                list.add(key.getStrGroup());
            }
            FunnelAggValue value = entry.getValue();
            int[] counts = value.getCount();
            for (int c : counts) {
                list.add(c);
            }
            double[] median = value.getMedians();
            if (median != null) {
                for (double m : median) {
                    list.add(m);
                }
            }
            rows.add(new ListBasedRow(list));
        }
        return new RowSwiftResultSet(metaData, rows);
    }

    @Override
    public void close() {

    }
}
