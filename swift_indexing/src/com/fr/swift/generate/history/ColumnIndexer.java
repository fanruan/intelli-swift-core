package com.fr.swift.generate.history;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

/**
 * This class created on 2017-12-28 10:54:47
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnIndexer<T extends Comparable<T>> extends BaseColumnIndexer<T> {
    public ColumnIndexer(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        baseColumn.release();
    }
}