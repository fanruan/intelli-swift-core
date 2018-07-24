package com.fr.swift.query.result.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.result.AbstractResultQuery;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;

import java.util.Comparator;
import java.util.List;

/**
 * Created by pony on 2017/12/18.
 */
public abstract class AbstractGroupResultQuery extends AbstractResultQuery<NodeResultSet> {

    protected List<Aggregator> aggregators;
    protected List<Comparator<GroupNode>> comparators;

    public AbstractGroupResultQuery(List<Query<NodeResultSet>> queries, List<Aggregator> aggregators,
                                    List<Comparator<GroupNode>> comparators) {
        super(queries);
        this.aggregators = aggregators;
        this.comparators = comparators;
    }
}
