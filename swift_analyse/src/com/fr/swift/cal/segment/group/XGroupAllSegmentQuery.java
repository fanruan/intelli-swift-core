package com.fr.swift.cal.segment.group;

import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.XGroupByUtils;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.result.GroupByResultSet;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/4/1.
 */
public class XGroupAllSegmentQuery extends GroupAllSegmentQuery {

    private List<Column> colDimensions;
    private List<Sort> xIndexSorts;
    private int[] xCursor;

    public XGroupAllSegmentQuery(List<Column> rowDimensions, List<Column> colDimensions, List<Column> metrics,
                                 List<Aggregator> aggregators, DetailFilter filter) {
        super(rowDimensions, metrics, aggregators, filter);
        this.colDimensions = colDimensions;
    }

    @Override
    public GroupByResultSet getQueryResult() {
        xCursor = new int[dimensions.size()];
        Arrays.fill(xCursor, DictionaryEncodedColumn.NOT_NULL_START_INDEX);
        return XGroupByUtils.query(dimensions, colDimensions, metrics, aggregators, filter, indexSorts, xIndexSorts,
                cursor, xCursor, -1, -1);
    }
}
