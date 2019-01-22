package com.fr.swift.repository.config;

import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.file.SwiftFileSystemType;
import com.fr.swift.repository.SwiftFileSystemConfig;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfig implements SwiftFileSystemConfig {
    @ConfigField
    private String fsName = "fs.defaultFS";
    @ConfigField
    private String hdfsHost = "127.0.0.1";
    @ConfigField
    private String hdfsPort = "9000";

    public HdfsRepositoryConfig(String fsName, String hdfsHost, String hdfsPort) {
        this.fsName = fsName;
        this.hdfsHost = hdfsHost;
        this.hdfsPort = hdfsPort;
    }

    public HdfsRepositoryConfig() {
    }

    public String getFsName() {
        return fsName;
    }

    public void setFsName(String fsName) {
        this.fsName = fsName;
    }

    public String getHdfsHost() {
        return hdfsHost;
    }

    public void setHdfsHost(String hdfsHost) {
        this.hdfsHost = hdfsHost;
    }

    public String getHdfsPort() {
        return hdfsPort;
    }

    public void setHdfsPort(String hdfsPort) {
        this.hdfsPort = hdfsPort;
    }

    public String getFullAddress() {
        return String.format("hdfs://%s:%s/", hdfsHost, hdfsPort);
    }

    @Override
    public SwiftFileSystemType getType() {
        return HdfsSystemType.HDFS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HdfsRepositoryConfig that = (HdfsRepositoryConfig) o;

        if (hdfsHost != null ? !hdfsHost.equals(that.hdfsHost) : that.hdfsHost != null) {
            return false;
        }
        return hdfsPort != null ? hdfsPort.equals(that.hdfsPort) : that.hdfsPort == null;
    }

    @Override
    public int hashCode() {
        int result = hdfsHost != null ? hdfsHost.hashCode() : 0;
        result = 31 * result + (hdfsPort != null ? hdfsPort.hashCode() : 0);
        return result;
    }
}
