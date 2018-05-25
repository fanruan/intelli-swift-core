package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.segment.SegmentKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentUnique extends UniqueKey implements IConfigSegment {

    private Conf<String> sourceKey = Holders.simple(StringUtils.EMPTY);

    private ObjectMapConf<Map<Integer, SegmentKey>> segments = Holders.objMap(new HashMap<Integer, SegmentKey>(), Integer.class, SegmentKey.class);

    public SegmentUnique(String sourceKey, List<SegmentKey> keys) {
        this.setSourceKey(sourceKey);
        this.setSegments(keys);
    }

    public SegmentUnique() {
    }

    @Override
    public List<SegmentKey> getSegments() {
        Map<Integer, SegmentKey> map = segments.get();
        int size = map.size();
        List<SegmentKey> result = new ArrayList<SegmentKey>();
        for (int i = 0; i < size; i++) {
            result.add(map.get(i));
        }
        // fixme 是不是需要 unmodified
        return result;
    }

    @Override
    public void setSegments(List<SegmentKey> segments) {
        int size = segments.size();
        for (int i = 0; i < size; i++) {
            this.segments.put(i, segments.get(i));
        }
    }

    @Override
    public String getSourceKey() {
        return sourceKey.get();
    }

    @Override
    public void setSourceKey(String sourceKey) {
        this.sourceKey.set(sourceKey);
    }

    @Override
    public void addSegment(SegmentKey segmentKey) {
        segments.put(segments.get().size(), segmentKey);
    }
}
