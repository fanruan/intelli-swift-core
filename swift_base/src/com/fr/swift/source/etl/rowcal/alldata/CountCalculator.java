package com.fr.swift.source.etl.rowcal.alldata;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

/**
 * Created by Handsome on 2018/2/24 0024 15:43
 */
public class CountCalculator implements AllDataCalculator {

    public static CountCalculator INSTANCE = new CountCalculator();

    @Override
    public double get(Segment[] segments, RowTraversal[] traversal, ColumnKey columnKey) {
        if(segments.length > 0 && traversal.length > 0 && columnKey != null) {
            double count = 0;
            for (int i = 0; i < traversal.length; i++) {
                count += (double) traversal[i].getCardinality();
            }
            return count;
        }
        return 0;
    }
}
