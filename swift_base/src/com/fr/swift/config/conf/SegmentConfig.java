package com.fr.swift.config.conf;

import com.fr.config.ConfigContext;
import com.fr.config.DefaultConfiguration;
import com.fr.config.holder.factory.Holders;
import com.fr.config.holder.impl.ObjectMapConf;
import com.fr.swift.config.ISegment;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yee
 * @date 2018/3/9
 */
public class SegmentConfig extends DefaultConfiguration {
    private final static String NAMESPACE = "segment_config";

    private static SegmentConfig config = null;

    private ObjectMapConf<Map<String, ISegment>> segmentHolder = Holders.objMap(new HashMap<String, ISegment>(), String.class, ISegment.class);


    public static SegmentConfig getInstance() {
        if (null == config) {
            config = ConfigContext.getConfigInstance(SegmentConfig.class);
        }
        return config;
    }

    public Map<String, ISegment> getAllSegments() {
        return segmentHolder.get();
    }

    public ISegment getSegmentByKey(String key) {
        return (ISegment) segmentHolder.get(key);
    }

    public void addSegments(ISegment... segments) {
        for (ISegment segment : segments) {
            segmentHolder.put(segment.getSourceKey(), segment);
        }
    }

    public void removeSegment(String key) {
        segmentHolder.remove(key);
    }

    public void modifySegment(ISegment segment) {
        ISegment segmentUnique = (ISegment) segmentHolder.get(segment.getSourceKey());
        segmentUnique.setSegments(segment.getSegments());
        segmentUnique.setSourceKey(segment.getSourceKey());
    }

    @Override
    public String getNameSpace() {
        return NAMESPACE;
    }
}
