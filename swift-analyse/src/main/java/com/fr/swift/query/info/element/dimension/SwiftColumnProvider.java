package com.fr.swift.query.info.element.dimension;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

/**
 * @author pony
 * @date 2017/12/11
 * swift中已经有的列,或者可以通过公式算出明细的列
 */
public interface SwiftColumnProvider extends QueryColumn {

    ColumnKey getColumnKey();

    Column getColumn(Segment segment);
}
