package com.fr.swift.db.impl;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftTable implements Table {
    private SourceKey key;
    private SwiftMetaData meta;

    public SwiftTable(SourceKey key, SwiftMetaData meta) {
        this.key = key;
        this.meta = meta;
    }

    @Override
    public SwiftMetaData getMeta() {
        return meta;
    }

    @Override
    public void setMeta(SwiftMetaData meta) {
        this.meta = meta;
    }

    @Override
    public void insert(SwiftResultSet rowSet) throws SQLException {
        try {
            Inserter inserter = SwiftContext.getInstance().getSwiftDataOperatorProvider().getRealtimeInserter(this);
            inserter.insertData(rowSet);
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
    }

    @Override
    public void importFrom(SwiftResultSet rowSet) throws SQLException {
        try {
            Inserter inserter = SwiftContext.getInstance().getSwiftDataOperatorProvider().getHistoryInserter(this);
            inserter.insertData(rowSet);
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
    }

    @Override
    public int delete(Where where) throws SQLException {
        SwiftResultSet rowSet = select(where);
        try {
            List<Segment> segments = SwiftContext.getInstance().getSegmentProvider().getSegment(key);
            // fixme 应传入整个segments
            Deleter deleter = SwiftContext.getInstance().getSwiftDataOperatorProvider().getSwiftDeleter(segments.get(0));
            deleter.deleteData(rowSet);
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
        return -1;
    }

    @Override
    public int update(Where where, SwiftResultSet rowSet) {
        return 0;
    }

    @Override
    public SwiftResultSet select(Where where) {
        return null;
    }

    @Override
    public SourceKey getSourceKey() {
        return key;
    }
}