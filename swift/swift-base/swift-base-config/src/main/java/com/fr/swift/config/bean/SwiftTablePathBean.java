package com.fr.swift.config.bean;

import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Constructor;

/**
 * @author yee
 * @date 2018-11-26
 */
public class SwiftTablePathBean implements ObjectConverter {
    public static final Class TYPE = entityType();
    private String clusterId;
    private String tableKey;
    private Integer tablePath;
    private Integer lastPath;
    private Integer tmpDir;

    public SwiftTablePathBean() {
    }

    public SwiftTablePathBean(String clusterId, String tableKey, Integer tablePath, Integer lastPath, Integer tmpDir) {
        this.clusterId = clusterId;
        this.tableKey = tableKey;
        this.tablePath = tablePath;
        this.lastPath = lastPath;
        this.tmpDir = tmpDir;
    }

    public SwiftTablePathBean(String tableKey, Integer tmpDir) {
        this.clusterId = SwiftConfigConstants.LOCALHOST;
        this.tableKey = tableKey;
        this.tmpDir = tmpDir;
    }

    private static Class entityType() {
        try {
            return Class.forName("com.fr.swift.config.entity.SwiftTablePathEntity");
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

    public String getTableKey() {
        return tableKey;
    }

    public void setTableKey(String tableKey) {
        this.tableKey = tableKey;
    }

    public Integer getTablePath() {
        return tablePath;
    }

    public void setTablePath(Integer tablePath) {
        this.tablePath = tablePath;
    }

    public Integer getLastPath() {
        return lastPath;
    }

    public void setLastPath(Integer lastPath) {
        this.lastPath = lastPath;
    }

    public Integer getTmpDir() {
        return tmpDir;
    }

    public void setTmpDir(Integer tmpDir) {
        this.tmpDir = tmpDir;
    }

    @Override
    public Object convert() {
        try {
            Constructor constructor = TYPE.getDeclaredConstructor(SwiftTablePathBean.class);
            return constructor.newInstance(this);
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
        return null;
    }
}
