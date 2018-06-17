package com.fr.swift.segment.column;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;

import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/6/13
 */
public class RealtimeDoubleColumn extends BaseRealtimeColumn<Double> {
    public RealtimeDoubleColumn(IResourceLocation location) {
        super(location);
        init();
    }

    @Override
    void init() {
        super.init();

        // 防止未初始化
        if (c == null) {
            c = Comparators.asc();
        }
        if (valToRows == null) {
            valToRows = new TreeMap<Double, MutableBitMap>(c);
        }
//        if (addedValues == null) {
//            addedValues = new TreeSet<Double>(c);
//        }
    }
}