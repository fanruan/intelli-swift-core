package com.fr.swift.config.bean;

import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2018-11-26
 */
public class SegLocationBean implements ObjectConverter {
    public static final Class TYPE = entityType();
    private String clusterId;
    private String segmentId;
    private String sourceKey;

    public SegLocationBean(String clusterId, String segmentId, String sourceKey) {
        this.clusterId = clusterId;
        this.segmentId = segmentId;
        this.sourceKey = sourceKey;
    }

    public SegLocationBean() {
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftSegmentLocationEntity");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SegLocationBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
