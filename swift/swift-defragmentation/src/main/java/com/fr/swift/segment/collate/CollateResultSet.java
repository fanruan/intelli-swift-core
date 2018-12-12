package com.fr.swift.segment.collate;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * This class created on 2018/4/24
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Deprecated
public class CollateResultSet implements SwiftResultSet {

    private int alloterCount;
    private int currentCount;
    private SwiftMetaData swiftMetaData;
    private SwiftResultSet swiftResultSet;
    private int currentSegment;

    public CollateResultSet(SwiftResultSet swiftResultSet, int alloterCount, SwiftMetaData swiftMetaData) {
        this.alloterCount = alloterCount;
        this.swiftMetaData = swiftMetaData;
        this.swiftResultSet = swiftResultSet;
        this.currentCount = 0;
        this.currentSegment = 0;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() {
        return swiftMetaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return (currentCount / alloterCount) <= currentSegment && swiftResultSet.hasNext();
    }

    @Override
    public Row getNextRow() throws SQLException {
        Row nextRow = swiftResultSet.getNextRow();
        currentCount++;
        return nextRow;
    }

    @Override
    public void close() {
        currentSegment++;
    }
}
