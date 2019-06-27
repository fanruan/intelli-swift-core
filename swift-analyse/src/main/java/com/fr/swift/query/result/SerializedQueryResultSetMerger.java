package com.fr.swift.query.result;

import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.query.query.QueryType;
import com.fr.swift.query.result.serialize.SerializedGroupQueryResultSet;
import com.fr.swift.query.result.serialize.SerializedSortedDetailQueryResultSet;
import com.fr.swift.result.DetailQueryResultSet;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.MergeDetailQueryResultSet;
import com.fr.swift.result.MergeSortedDetailQueryResultSet;
import com.fr.swift.result.funnel.MergeFunnelQueryResultSet;
import com.fr.swift.result.node.resultset.MergeGroupQueryResultSet;
import com.fr.swift.result.qrs.EmptyQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/6/21
 */
public class SerializedQueryResultSetMerger {

    public static QueryResultSet<?> merge(QueryType queryType, List<QueryResultSet<?>> resultSets) {
        if (resultSets == null || resultSets.isEmpty()) {
            return EmptyQueryResultSet.get();
        }
        // TODO: 2019/6/27 anchore resultset未实现toSwiftResultSet方法，会挂，后面去掉convert后，这里应该能开出来
//        if (resultSets.size() == 1) {
//            return resultSets.get(0);
//        }
        switch (queryType) {
            case GROUP: {
                List<QueryResultSet<GroupPage>> serializedResultSets = new ArrayList<QueryResultSet<GroupPage>>(resultSets.size());
                for (QueryResultSet<?> resultSet : resultSets) {
                    serializedResultSets.add((QueryResultSet<GroupPage>) resultSet);
                }
                SerializedGroupQueryResultSet first = (SerializedGroupQueryResultSet) serializedResultSets.get(0);
                return new MergeGroupQueryResultSet(first.getFetchSize(), first.getGlobalIndexed(), serializedResultSets, first.getAggregators(), first.getComparators());
            }
            case DETAIL: {
                List<DetailQueryResultSet> serializedResultSets = new ArrayList<DetailQueryResultSet>(resultSets.size());
                for (QueryResultSet<?> resultSet : resultSets) {
                    serializedResultSets.add((DetailQueryResultSet) resultSet);
                }
                DetailQueryResultSet first = serializedResultSets.get(0);
                return new MergeDetailQueryResultSet(first.getFetchSize(), serializedResultSets);
            }
            case DETAIL_SORT: {
                List<DetailQueryResultSet> serializedResultSets = new ArrayList<DetailQueryResultSet>(resultSets.size());
                for (QueryResultSet<?> resultSet : resultSets) {
                    serializedResultSets.add((DetailQueryResultSet) resultSet);
                }
                SerializedSortedDetailQueryResultSet first = (SerializedSortedDetailQueryResultSet) serializedResultSets.get(0);
                return new MergeSortedDetailQueryResultSet(first.getFetchSize(), first.getComparator(), serializedResultSets);
            }
            case FUNNEL: {
                List<QueryResultSet<FunnelResultSet>> serializedResultSets = new ArrayList<QueryResultSet<FunnelResultSet>>(resultSets.size());
                for (QueryResultSet<?> resultSet : resultSets) {
                    serializedResultSets.add((QueryResultSet<FunnelResultSet>) resultSet);
                }
                MergeFunnelQueryResultSet first = (MergeFunnelQueryResultSet) serializedResultSets.get(0);
                return new MergeFunnelQueryResultSet(serializedResultSets, first.getStepCount());
            }
            default:
                return EmptyQueryResultSet.get();
        }
    }
}