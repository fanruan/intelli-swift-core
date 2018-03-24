package com.fr.swift.cal.segment.group;

import com.fr.swift.constant.SwiftGroupByConstants;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.GroupByUtils;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.column.Column;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pony on 2017/12/18.
 */
public class GroupAllSegmentQuery extends AbstractGroupSegmentQuery{

    private List<Sort> indexSorts;

    public GroupAllSegmentQuery(List<Column> dimensions, List<Column> metrics, List<Aggregator> aggregators, DetailFilter filter) {
        super(dimensions, metrics, aggregators, filter);
    }

    @Override
    public GroupByResultSet getQueryResult() {
        int[] cursor = new int[dimensions.size()];
        Arrays.fill(cursor, SwiftGroupByConstants.DICTIONARY.NOT_NULL_START_INDEX);
        return GroupByUtils.query(dimensions, metrics, aggregators, filter, indexSorts, cursor, -1);
    }
}
