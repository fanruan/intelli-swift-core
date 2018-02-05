package com.fr.swift.source.etl.join;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.source.etl.utils.AggregatorValueCollection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Handsome on 2017/12/6 0006 14:20
 */
class SwiftValuesAndGVI {

    Object[] values;
    ImmutableBitMap gvi;
    List<AggregatorValueCollection> aggreator = new ArrayList<AggregatorValueCollection>();

    public SwiftValuesAndGVI(Object[] values, ImmutableBitMap gvi) {
        this.values = values;
        this.gvi = gvi;
    }

    public int compareTo(SwiftValuesAndGVI o, Comparator[] comparators) {
        if (o == null) {
            return -1;
        }
        for (int i = 0; i < values.length; i++) {
            int result = comparators[i].compare(values[i], o.values[i]);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

}
