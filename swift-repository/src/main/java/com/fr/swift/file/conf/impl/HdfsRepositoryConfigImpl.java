package com.fr.swift.file.conf.impl;

import com.fr.swift.file.conf.AbstractSwiftFileSystemConfig;
import com.fr.swift.file.system.SwiftFileSystemType;

/**
 * @author yee
 * @date 2018/6/15
 */
public class HdfsRepositoryConfigImpl extends AbstractSwiftFileSystemConfig {

    private String hdfsHost;
    private String hdfsPort;

    public HdfsRepositoryConfigImpl(String hdfsHost, String hdfsPort) {
        this.hdfsHost = hdfsHost;
        this.hdfsPort = hdfsPort;
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
        return String.format("hdfs://%s:%s", hdfsHost, hdfsPort);
    }

    @Override
    public SwiftFileSystemType getType() {
        return SwiftFileSystemType.HDFS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HdfsRepositoryConfigImpl that = (HdfsRepositoryConfigImpl) o;

        if (hdfsHost != null ? !hdfsHost.equals(that.hdfsHost) : that.hdfsHost != null) return false;
        return hdfsPort != null ? hdfsPort.equals(that.hdfsPort) : that.hdfsPort == null;
    }

    @Override
    public int hashCode() {
        int result = hdfsHost != null ? hdfsHost.hashCode() : 0;
        result = 31 * result + (hdfsPort != null ? hdfsPort.hashCode() : 0);
        return result;
    }
}
