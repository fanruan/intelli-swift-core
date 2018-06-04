package com.fr.swift.query.post;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.query.sort.SortType;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/6/3.
 */
public class RowSortQuery extends AbstractPostQuery<NodeResultSet> {

    private PostQuery<NodeResultSet> query;
    private List<Sort> sortList;

    public RowSortQuery(PostQuery<NodeResultSet> query, List<Sort> sortList) {
        this.query = query;
        this.sortList = sortList;
    }

    @Override
    public NodeResultSet getQueryResult() throws SQLException {
        // 这个排序完之后没法构建Node了，维度顺序被打乱不满足构建树的前提条件了
        NodeMergeResultSet resultSet = (NodeMergeResultSet) query.getQueryResult();
        int dimensionSize = resultSet.getRowGlobalDictionaries().size();
        Iterator<List<SwiftNode>> rowIt = new Tree2RowIterator<SwiftNode>(dimensionSize,
                resultSet.getNode().getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
            @Override
            public Iterator<SwiftNode> apply(SwiftNode p) {
                return p.getChildren().iterator();
            }
        });
        List<List<SwiftNode>> rows = IteratorUtils.iterator2List(rowIt);
        sortRow(dimensionSize, rows, sortList);
        // TODO: 2018/6/4 这边返回啥好呢？
        return null;
    }

    private static void sortRow(final int dimensionSize, List<List<SwiftNode>> rows, final List<Sort> sorts) {
        Collections.sort(rows, new Comparator<List<SwiftNode>>() {
            @Override
            public int compare(List<SwiftNode> o1, List<SwiftNode> o2) {
                AggregatorValue[] values1 = o1.get(o1.size() - 1).getAggregatorValue();
                AggregatorValue[] values2 = o2.get(o2.size() - 1).getAggregatorValue();
                for (Sort sort : sorts) {
                    Number v1 = (Number) values1[sort.getTargetIndex() - dimensionSize].calculateValue();
                    Number v2 = (Number) values2[sort.getTargetIndex() - dimensionSize].calculateValue();
                    if (v1 == null) {
                        return 1;
                    }
                    if (v2 == null) {
                        return -1;
                    }
                    if (v1.doubleValue() == v2.doubleValue()) {
                        continue;
                    }
                    boolean v = v1.doubleValue() < v2.doubleValue();
                    return (sort.getSortType() == SortType.ASC == v) ? -1 : 1;
                }
                return 0;
            }
        });
    }
}
