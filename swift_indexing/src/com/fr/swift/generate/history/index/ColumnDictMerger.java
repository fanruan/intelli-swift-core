package com.fr.swift.generate.history.index;

import com.fr.swift.cube.io.Releasable;
import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

/**
 * @author anchore
 * @date 2018/1/9
 * <p>
 * 合并字典
 */
public class ColumnDictMerger<T extends Comparable<T>> extends BaseColumnDictMerger<T> {
    public ColumnDictMerger(DataSource dataSource, ColumnKey key) {
        super(dataSource, key);
    }

    @Override
    protected void releaseIfNeed(Releasable baseColumn) {
        baseColumn.release();
    }
}