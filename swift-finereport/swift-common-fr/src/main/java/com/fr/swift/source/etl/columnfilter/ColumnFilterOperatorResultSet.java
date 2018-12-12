package com.fr.swift.source.etl.columnfilter;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.FilterBuilder;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Handsome on 2018/1/24 0024 15:34
 */
public class ColumnFilterOperatorResultSet implements SwiftResultSet {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ColumnFilterOperatorResultSet.class);

    private Segment[] segment;

    /**
     * 可能字段改过名，底层取数据用这个
     */
    private SwiftMetaData baseMeta;

    private SwiftMetaData metaData;

    private FilterInfo filterInfo;
    private int segCursor = 0;
    private DictionaryEncodedColumn[] dics;
    private Iterator<Integer> currentSegRowIter;

    public ColumnFilterOperatorResultSet(Segment[] segment, SwiftMetaData baseMeta, SwiftMetaData metaData, FilterInfo filterInfo) {
        this.segment = segment;
        this.baseMeta = baseMeta;
        this.metaData = metaData;
        this.filterInfo = filterInfo;
        moveNextSegment();
    }

    public ColumnFilterOperatorResultSet(Segment[] segment, SwiftMetaData metaData, FilterInfo filterInfo) {
        this(segment, metaData, metaData, filterInfo);
    }

    private void moveNextSegment() {
        final List<Integer> rows = new ArrayList<Integer>();
        if (segCursor >= segment.length) {
            currentSegRowIter = null;
            return;
        }
        try {
            dics = new DictionaryEncodedColumn[baseMeta.getColumnCount()];
            for (int i = 0; i < baseMeta.getColumnCount(); i++) {
                dics[i] = segment[segCursor].getColumn(new ColumnKey(baseMeta.getColumn(i + 1).getName())).getDictionaryEncodedColumn();
            }
            segment[segCursor].getAllShowIndex().getAnd(createFilter(segment[segCursor])).traversal(new TraversalAction() {
                @Override
                public void actionPerformed(int row) {
                    rows.add(row);
                }
            });
            currentSegRowIter = rows.iterator();
        } catch (SwiftMetaDataException e) {
            LOGGER.error("getting meta's column information failed", e);
        }
        segCursor++;
    }

    @Override
    public void close() throws SQLException {

    }

    private ImmutableBitMap createFilter(Segment segment) {
        return FilterBuilder.buildDetailFilter(segment, filterInfo).createFilterIndex();
    }

    @Override
    public boolean hasNext() {
        while (currentSegRowIter != null && !currentSegRowIter.hasNext()) {
            moveNextSegment();
        }
        return currentSegRowIter != null && currentSegRowIter.hasNext();
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getNextRow() throws SQLException {
        List<Object> list = new ArrayList<Object>();
        int row = currentSegRowIter.next();
        for (DictionaryEncodedColumn dic : dics) {
            list.add(dic.getValueByRow(row));
        }
        return new ListBasedRow(list);
    }

}
