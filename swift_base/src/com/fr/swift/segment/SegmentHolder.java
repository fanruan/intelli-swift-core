package com.fr.swift.segment;

import com.fr.swift.cube.io.Types;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;

/**
 * This class created on 2018-1-10 10:51:01
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public interface SegmentHolder {

    Segment getSegment();

    DetailColumn getColumn(String columnName);

    DetailColumn getColumn(ColumnKey columnKey);

    int incrementRowCount();

    void putRowCount();

    void putAllShowIndex();

    void putNullIndex();

    void putDetail(int column, Object value) throws SwiftMetaDataException;

    void release();

    Types.StoreType getStoreType();
}

