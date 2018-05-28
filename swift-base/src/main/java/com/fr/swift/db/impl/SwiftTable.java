package com.fr.swift.db.impl;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Deleter;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.core.Core;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/28
 */
class SwiftTable implements Table {
    private SwiftDataOperatorProvider operators = SwiftContext.getInstance().getBean(SwiftDataOperatorProvider.class);

    private SourceKey key;

    private SwiftMetaData meta;

    SwiftTable(SourceKey key, SwiftMetaData meta) {
        this.key = key;
        this.meta = meta;
    }

    @Override
    public SwiftMetaData getMeta() {
        return getMetadata();
    }

    @Override
    public void setMeta(SwiftMetaData meta) {
        this.meta = meta;
    }

    @Override
    public void insert(SwiftResultSet rowSet) throws SQLException {
        try {
            Inserter inserter = operators.getRealtimeBlockSwiftInserter(this);
            List<Segment> segments = inserter.insertData(rowSet);
            for (String field : inserter.getFields()) {
                operators.getColumnIndexer(this, new ColumnKey(field), segments).buildIndex();
                operators.getColumnDictMerger(this, new ColumnKey(field), segments).mergeDict();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
    }

    @Override
    public void importFrom(SwiftResultSet rowSet) throws SQLException {
        try {
            // 调流程
            Inserter inserter = operators.getHistoryBlockSwiftInserter(this);
            List<Segment> segments = inserter.insertData(rowSet);
            for (String field : inserter.getFields()) {
                operators.getColumnIndexer(this, new ColumnKey(field), segments).buildIndex();
                operators.getColumnDictMerger(this, new ColumnKey(field), segments).mergeDict();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
    }

    @Override
    public int delete(Where where) throws SQLException {
        // todo 这里应该是从数据库查出来的结果集
        SwiftResultSet rowSet = null;
        try {
            List<Segment> segments = SwiftContext.getInstance().getBean(SwiftSegmentManager.class).getSegment(key);
            // fixme 应传入整个segments
            Deleter deleter = operators.getSwiftDeleter(segments.get(0));
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

    @Override
    public SwiftMetaData getMetadata() {
        return meta;
    }

    @Override
    public Core fetchObjectCore() {
        return null;
    }
}