package com.fr.swift.config.unique;

import com.fr.config.holder.Conf;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectColConf;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.config.utils.UniqueKey;
import com.fr.stable.StringUtils;
import com.fr.swift.config.IConfigSegment;
import com.fr.swift.config.ISegmentKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentUnique extends UniqueKey implements IConfigSegment {

    private Conf<String> sourceKey = Holders.simple(StringUtils.EMPTY);

    private ObjectMapConf<Map<String, ISegmentKey>> segments = Holders.objMap(new HashMap<String, ISegmentKey>(), String.class, ISegmentKey.class);

    public SegmentUnique(String sourceKey, Map<String, ISegmentKey> keys) {
        this.setSourceKey(sourceKey);
        this.setSegments(keys);
    }

    public SegmentUnique() {

    }

    public Map<String, ISegmentKey> getSegments() {
        return (Map<String, ISegmentKey>) segments.get();
    }

    public void setSegments(Map<String, ISegmentKey> segments) {
        this.segments.set(segments);
    }

    public String getSourceKey() {
        return sourceKey.get();
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey.set(sourceKey);
    }

    @Override
    public void addOrUpdateSegment(ISegmentKey segmentKey) {
        segments.put(segmentKey.getName(), segmentKey);
    }
}
