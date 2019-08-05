package com.fr.swift.query.result.serialize;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.SwiftNode;

import java.util.Comparator;
import java.util.List;

/**
 * @author lyon
 * @date 2018/12/29
 */
public class SerializedGroupQueryResultSet extends BaseSerializedQueryResultSet<GroupPage> {
    private static final long serialVersionUID = -1968765435028786800L;

    private boolean[] globalIndexed;
    private List<Aggregator> aggregators;
    private List<Comparator<SwiftNode>> comparators;

    public SerializedGroupQueryResultSet(int fetchSize,
                                         boolean[] globalIndexed,
                                         List<Aggregator> aggregators,
                                         List<Comparator<SwiftNode>> comparators,
                                         GroupPage page, boolean hasNextPage) {
        super(fetchSize, page, hasNextPage);
        this.comparators = comparators;
        this.globalIndexed = globalIndexed;
        this.aggregators = aggregators;
    }

    public boolean[] getGlobalIndexed() {
        return globalIndexed;
    }

    public List<Aggregator> getAggregators() {
        return aggregators;
    }

    public List<Comparator<SwiftNode>> getComparators() {
        return comparators;
    }
}
