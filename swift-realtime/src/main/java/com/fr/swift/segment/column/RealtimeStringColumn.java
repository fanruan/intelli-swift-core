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
public class RealtimeStringColumn extends BaseRealtimeColumn<String> {
    public RealtimeStringColumn(IResourceLocation location) {
        super(location);
        init();
    }

    @Override
    void init() {
        super.init();

        // 防止未初始化
        c = c == null ? Comparators.PINYIN_ASC : c;
        valToRows = valToRows == null ? new TreeMap<String, MutableBitMap>(c) : valToRows;
        cutInValues = cutInValues == null ? new TreeSet<String>(c) : cutInValues;
    }
}