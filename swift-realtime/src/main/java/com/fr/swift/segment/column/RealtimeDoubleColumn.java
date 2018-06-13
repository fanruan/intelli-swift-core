package com.fr.swift.segment.column;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;

import java.util.TreeMap;
import java.util.TreeSet;

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
        c = c == null ? Comparators.<Double>asc() : c;
        valToRows = valToRows == null ? new TreeMap<Double, MutableBitMap>(c) : valToRows;
        cutInValues = cutInValues == null ? new TreeSet<Double>(c) : cutInValues;
    }
}