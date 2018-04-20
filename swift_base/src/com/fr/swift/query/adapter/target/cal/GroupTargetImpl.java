package com.fr.swift.query.adapter.target.cal;

import com.fr.swift.query.adapter.AbstractQueryColumn;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.info.FilterInfo;

/**
 * Created by Lyon on 2018/4/19.
 */
public class GroupTargetImpl extends AbstractQueryColumn implements GroupTarget {

    private int resultIndex;
    private int[] paramIndexes;
    private CalTargetType type;
    private FilterInfo filterInfo;
    private Aggregator aggregator;

    public GroupTargetImpl(int queryIndex, int resultIndex, int[] paramIndexes, CalTargetType type,
                           FilterInfo filterInfo, Aggregator aggregator) {
        super(queryIndex);
        this.resultIndex = resultIndex;
        this.paramIndexes = paramIndexes;
        this.type = type;
        this.filterInfo = filterInfo;
        this.aggregator = aggregator;
    }

    @Override
    public Aggregator getAggregator() {
        return aggregator;
    }

    @Override
    public FilterInfo getFilter() {
        return filterInfo;
    }

    @Override
    public int[] paramIndexes() {
        return paramIndexes;
    }

    @Override
    public int resultIndex() {
        return resultIndex;
    }

    @Override
    public CalTargetType type() {
        return type;
    }
}
