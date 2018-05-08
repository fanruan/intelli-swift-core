package com.fr.swift.generate.history.index;

import com.fr.swift.generate.BaseColumnIndexer;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2017-12-28 10:54:47
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class ColumnIndexer<T> extends BaseColumnIndexer<T> {
    public ColumnIndexer(DataSource dataSource, ColumnKey key, List<Segment> segments) {
        super(dataSource, key, segments);
    }
}