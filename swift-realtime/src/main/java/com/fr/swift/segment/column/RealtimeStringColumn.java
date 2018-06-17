package com.fr.swift.segment.column;

import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;

import java.util.TreeMap;

/**
 * @author anchore
 * @date 2018/6/13
 */
public class RealtimeStringColumn extends BaseRealtimeColumn<String> {
    public RealtimeStringColumn(IResourceLocation location) {
        super(location);
    }

    @Override
    void init() {
        super.init();

        // 防止未初始化
        if (c == null) {
            c = Comparators.PINYIN_ASC;
        }
        if (valToRows == null) {
            valToRows = new TreeMap<String, MutableBitMap>(c);
        }
    }
}