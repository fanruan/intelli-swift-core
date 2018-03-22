package com.fr.swift.generate.realtime;

import com.fr.general.ComparatorUtils;
import com.fr.swift.cube.io.Releasable;
import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.ColumnTypeConstants.ClassType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.util.Iterator;
import java.util.List;

/**
 * This class created on 2018-1-22 10:53:18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class RealtimeColumnIndexer<T extends Comparable<T>> extends BaseColumnIndexer<T> {
    protected DataSource dataSource;

    public RealtimeColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(key);
        this.dataSource = dataSource;
    }

    @Override
    protected List<Segment> getSegments() {
        List<Segment> segmentList = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        Iterator<Segment> segmentIterator = segmentList.iterator();
        while (segmentIterator.hasNext()) {
            Segment segment = segmentIterator.next();
            if (ComparatorUtils.equals(segment.getLocation().getStoreType(), Types.StoreType.FINE_IO)) {
                segmentIterator.remove();
            }
        }
        return segmentList;
    }


    @Override
    protected void mergeDict() {
        new RealtimeColumnDictMerger<T>(dataSource, key).work();
    }

    @Override
    protected ClassType getClassType() {
        try {
            SwiftMetaDataColumn metaColumn = dataSource.getMetadata().getColumn(key.getName());
            return ColumnTypeUtils.sqlTypeToClassType(
                    metaColumn.getType(),
                    metaColumn.getPrecision(),
                    metaColumn.getScale()
            );
        } catch (SwiftMetaDataException e) {
            return Crasher.crash(e);
        }
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        // 内存的column不释放，以后还有用
    }
}