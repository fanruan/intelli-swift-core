package com.fr.swift.cloud.entity;

import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Table;

import java.util.Date;

/**
 * @author yee
 * @date 2018-12-21
 */
@Entity
@Table(name = "cloud_web_container")
public class CloudWebContainer {
    @Column
    private Date time;
    @Column
    private String containerType;
    @Column
    private long containerMem;
    @Column
    private int cpu;
    @Column
    private String disk;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public long getContainerMem() {
        return containerMem;
    }

    public void setContainerMem(long containerMem) {
        this.containerMem = containerMem;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }
}
