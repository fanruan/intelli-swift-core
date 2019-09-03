package com.fr.swift.config.entity;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;

import java.io.Serializable;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-03
 */
@Entity
@Table(name = "fine_swift_jdbc_server")
public class SwiftJdbcServerInfo implements Serializable {
    private static final long serialVersionUID = -2770151852252847453L;
    @Id
    private String clusterId;
    @Column(name = "host", precision = 255)
    private String host;
    @Column(name = "port")
    private int port;
    @Column(name = "version", precision = 255)
    private String version;

    public SwiftJdbcServerInfo(String clusterId, String host, int port, String version) {
        this.clusterId = clusterId;
        this.host = host;
        this.port = port;
        this.version = version;
    }

    public SwiftJdbcServerInfo() {
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
