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
@Table(name = "cloud_package")
public class CloudPackage {
    @Column
    private String appName;
    @Column
    private String appId;
    @Column
    private Date time;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
