package com.fr.swift.result;

import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.result.qrs.QueryResultSetMerger;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Xiaolei.Liu on 2018/1/23
 *
 * @author yee
 */

public class MultiSegmentDetailResultSet extends BaseDetailQueryResultSet {

    private int rowCount;
    /**
     * mergeIterator和rowIterator看似相同，其实不然，前者可以理解为内部实现(处理翻页缓存等)，后者为外部实现(对应SwiftResult)
     */
    private Iterator<Row> mergeIterator;
    private IDetailQueryResultSetMerger merger;

    public MultiSegmentDetailResultSet(int fetchSize, int rowCount, DetailQueryResultSetMerger.DetailRowIterator queries,
                                       IDetailQueryResultSetMerger merger) {
        super(fetchSize);
        this.mergeIterator = queries;
        this.rowCount = rowCount;
        this.merger = merger;
    }

    @Override
    public List<Row> getPage() {
        List<Row> rows = new ArrayList<Row>();
        int count = fetchSize;
        while (mergeIterator.hasNext() && count-- > 0) {
            rows.add(mergeIterator.next());
        }
        return rows;
    }

    @Override
    public boolean hasNextPage() {
        return mergeIterator.hasNext();
    }

    @Override
    public <Q extends QueryResultSet<List<Row>>> QueryResultSetMerger<List<Row>, Q> getMerger() {
        return (QueryResultSetMerger<List<Row>, Q>) merger;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public SwiftResultSet convert(final SwiftMetaData metaData) {
        return SortMultiSegmentDetailResultSet.create(fetchSize, metaData, this);
    }
}
