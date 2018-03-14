package com.fr.swift.config;

import com.fr.swift.config.unique.SegmentKeyUnique;
import com.fr.swift.config.unique.SegmentUnique;
import com.fr.swift.cube.io.Types;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentCreater {
    public static SegmentUnique getSegment() {
        SegmentUnique segmentUnique = new SegmentUnique();
        segmentUnique.setSourceKey("sourceA");
        List<ISegmentKey> keyUniques = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SegmentKeyUnique keyUnique = new SegmentKeyUnique("seg" + i, URI.create("/seg" + i), i, Types.StoreType.FINE_IO);
            keyUniques.add(keyUnique);
        }
        segmentUnique.setSegments(keyUniques);
        return segmentUnique;
    }

    public static SegmentUnique getModify() {
        SegmentUnique segmentUnique = new SegmentUnique();
        segmentUnique.setSourceKey("sourceA");
        List<ISegmentKey> keyUniques = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            SegmentKeyUnique keyUnique = new SegmentKeyUnique("seg" + (i + 1), URI.create("/seg" + (i + 1)), i, Types.StoreType.FINE_IO);
            keyUniques.add(keyUnique);
        }
        segmentUnique.setSegments(keyUniques);
        return segmentUnique;
    }
}
