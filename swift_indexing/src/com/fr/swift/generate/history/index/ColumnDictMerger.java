package com.fr.swift.generate.history.index;

import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ColumnDictMerger<T> extends BaseColumnDictMerger<T> {
    public ColumnDictMerger(DataSource dataSource, ColumnKey key, List<Segment> segments) {
        super(dataSource, key, segments);
    }

}
