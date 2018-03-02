package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:13
 */
public interface AllDataCalculator {

    double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey);

}
