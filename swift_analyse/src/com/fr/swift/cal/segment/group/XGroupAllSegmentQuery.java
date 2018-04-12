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
    private List<Sort> colIndexSorts;
    private int[] xCursor;

    public XGroupAllSegmentQuery(List<Column> rowDimensions, List<Column> colDimensions, List<Column> metrics,
                                 List<Aggregator> aggregators, DetailFilter filter,
                                 List<Sort> rowIndexSorts, List<Sort> colIndexSorts) {
        super(rowDimensions, metrics, aggregators, filter, rowIndexSorts);
        this.colDimensions = colDimensions;
        this.colIndexSorts = colIndexSorts;
    }

    @Override
    public GroupByResultSet getQueryResult() {
        cursor = new int[dimensions.size()];
        Arrays.fill(cursor, 0);
        xCursor = new int[colDimensions.size()];
        Arrays.fill(xCursor, 0);
        return XGroupByUtils.query(dimensions, colDimensions, metrics, aggregators, filter, indexSorts, colIndexSorts,
                cursor, xCursor, -1, -1);
    }
}
