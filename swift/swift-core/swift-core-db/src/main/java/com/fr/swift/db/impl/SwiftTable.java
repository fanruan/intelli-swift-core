package com.fr.swift.db.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.segment.operator.column.SwiftColumnDictMerger;
import com.fr.swift.segment.operator.column.SwiftColumnIndexer;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.alloter.impl.line.LineSourceAlloter;
import com.fr.swift.source.core.Core;

import java.sql.SQLException;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/28
 */
class SwiftTable implements Table {
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
    public void insert(SwiftResultSet rowSet) throws SQLException {
        try {
            LineSourceAlloter alloter = new LineSourceAlloter(getSourceKey(), new LineAllotRule(LineAllotRule.MEM_STEP));
            Inserter inserter = (Inserter) SwiftContext.get().getBean("incrementer", this, alloter);
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
            // 调流程
            Inserter inserter = (Inserter) SwiftContext.get().getBean("historyBlockInserter", this);
            inserter.insertData(rowSet);
            List<Segment> segments = SwiftContext.get().getBean("indexingSegmentManager", SwiftSegmentManager.class).getSegment(key);
            for (String field : inserter.getFields()) {
                ((SwiftColumnIndexer) SwiftContext.get().getBean("columnIndexer", this, new ColumnKey(field), segments)).buildIndex();
                ((SwiftColumnDictMerger) SwiftContext.get().getBean("columnDictMerger", this, new ColumnKey(field), segments)).mergeDict();
            }
        } catch (Exception e) {
            throw new SQLException(e);
        } finally {
            rowSet.close();
        }
    }

    @Override
    public int delete(Where where) {
        // todo 这里应该是从数据库查出来的结果集
//        SwiftResultSet rowSet = null;
//        try {
//            List<Segment> segments = SwiftContext.get().getBean(SwiftSegmentManager.class).getSegment(key);
//            // fixme 应传入整个segments
//            Deleter deleter = operators.getSwiftDeleter(segments.get(0));
//            deleter.deleteData(rowSet);
//        } catch (Exception e) {
//            throw new SQLException(e);
//        } finally {
//            rowSet.close();
//        }
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

    @Override
    public String toString() {
        return getSourceKey().getId();
    }
}