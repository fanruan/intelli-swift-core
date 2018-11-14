package com.fr.swift.node;

import java.io.Serializable;

/**
 * This class created on 2018/6/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftClusterNodeImpl implements SwiftClusterNode, Serializable {
    private static final long serialVersionUID = -3201514069990203301L;
    private String id;
    private String name;
    private String ip;
    private int port;

    public SwiftClusterNodeImpl(String id, String name, String ip, int port) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "SwiftClusterNodeImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
