package com.fr.swift.config.bean;

import com.sun.management.OperatingSystemMXBean;

import java.io.Serializable;
import java.lang.management.ManagementFactory;

/**
 * @author yee
 * @date 2018/6/12
 */
public class ServerCurrentStatus implements Serializable, Comparable<ServerCurrentStatus> {
    private static final long serialVersionUID = -6488082118138038698L;
    private String clusterId;
    private double systemCpuLoad;
    private int processors;
    private long freeMemory;

    public ServerCurrentStatus(String clusterId) {
        OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.freeMemory = mb.getFreePhysicalMemorySize();
        this.systemCpuLoad = mb.getSystemCpuLoad();
        this.processors = mb.getAvailableProcessors();
        this.clusterId = clusterId;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public String getClusterId() {
        return clusterId;
    }

    /**
     * 优先选择最空闲的
     * 一样空闲就选择剩余内存最多的
     * 剩余内存一样就选processor数大的
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(ServerCurrentStatus o) {
        int compare = Double.compare(systemCpuLoad, o.systemCpuLoad);
        if (compare == 0) {
            compare = (systemCpuLoad < o.systemCpuLoad) ? 1 : ((systemCpuLoad == o.systemCpuLoad) ? 0 : -1);
            if (compare == 0) {
                return 0 - (processors - o.processors);
            }
            return compare;
        }
        return compare;
    }

    @Override
    public String toString() {
        return "ServerCurrentStatus{" +
                "clusterId='" + clusterId + '\'' +
                ", systemCpuLoad=" + systemCpuLoad +
                ", processors=" + processors +
                ", freeMemory=" + freeMemory +
                '}';
    }
}
