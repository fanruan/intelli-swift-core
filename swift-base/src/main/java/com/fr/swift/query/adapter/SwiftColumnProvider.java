package com.fr.swift.query.adapter;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;

/**
 * @author pony
 * @date 2017/12/11
 * swift中已经有的列
 */
public interface SwiftColumnProvider extends QueryColumn {
    SourceKey getSourceKey();

    ColumnKey getColumnKey();

    Column getColumn(Segment segment);
}
