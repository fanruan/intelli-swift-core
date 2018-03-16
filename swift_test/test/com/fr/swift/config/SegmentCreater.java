package com.fr.swift.config;

import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.cube.io.Types;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentCreater {
    public static SegmentUnique getSegment() {
        SegmentUnique segmentUnique = new SegmentUnique();
        segmentUnique.setSourceKey("sourceA");
        Map<String, ISegmentKey> keyUniques = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            SegmentKeyUnique keyUnique = new SegmentKeyUnique("seg" + i, URI.create("/seg" + i), i, Types.StoreType.FINE_IO);
            keyUniques.put(keyUnique.getName(), keyUnique);
        }
        segmentUnique.setSegments(keyUniques);
        return segmentUnique;
    }

    public static SegmentUnique getModify() {
        SegmentUnique segmentUnique = new SegmentUnique();
        segmentUnique.setSourceKey("sourceA");
        Map<String, ISegmentKey> keyUniques = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            SegmentKeyUnique keyUnique = new SegmentKeyUnique("seg" + (i + 1), URI.create("/seg" + (i + 1)), i, Types.StoreType.FINE_IO);
            keyUniques.put(keyUnique.getName(), keyUnique);
        }
        segmentUnique.setSegments(keyUniques);
        return segmentUnique;
    }
}
