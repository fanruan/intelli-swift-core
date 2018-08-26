package com.fr.swift.config;

import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.Schema;
import com.fr.swift.segment.SegmentKey;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentCreater {
    public static List<SegmentKey> getSegment() {
        String sourceKey = "sourceA";
        List<SegmentKey> keyUniques = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            keyUniques.add(new SegmentKeyBean(sourceKey, URI.create("/seg" + i), i, Types.StoreType.FINE_IO, Schema.CUBE));
        }
        return keyUniques;
    }

    public static List<SegmentKey> getModify() {
        String sourceKey = "sourceA";
        List<SegmentKey> keyUniques = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            keyUniques.add(new SegmentKeyBean(sourceKey, URI.create("/seg" + (i + 1)), i, Types.StoreType.FINE_IO, Schema.CUBE));
        }
        return keyUniques;
    }
}
