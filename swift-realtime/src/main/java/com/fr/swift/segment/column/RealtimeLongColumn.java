package com.fr.swift.segment.column;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author anchore
 * @date 2018/6/7
 */
public class RealtimeLongColumn extends BaseRealtimeColumn<Long> {
    public RealtimeLongColumn(IResourceLocation location) {
        super(location);
        init();
    }

    @Override
    void init() {
        super.init();

        // 防止未初始化
        c = c == null ? Comparators.<Long>asc() : c;
        valToRows = valToRows == null ? new TreeMap<Long, MutableBitMap>(c) : valToRows;
        cutInValues = cutInValues == null ? new TreeSet<Long>(c) : cutInValues;
    }
}