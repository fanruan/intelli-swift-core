package com.fr.swift.generate.realtime;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

/**
 * @author anchore
 * @date 2018/2/26
 */
public class RealtimeColumnDictMerger<T extends Comparable<T>> extends BaseColumnDictMerger<T> {
    public RealtimeColumnDictMerger(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        // realtime的不释放
    }
}