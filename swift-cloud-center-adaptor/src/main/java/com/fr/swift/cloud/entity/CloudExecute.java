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
@Table(name = "cloud_execute")
public class CloudExecute {
    @Column(name = "id")
    private String id;
    @Column(name = "tname")
    private String templateName;
    @Column(name = "displayName")
    private String displayName;
    @Column(name = "time")
    private Date time;
    @Column(name = "memory")
    private double memory;
    @Column(name = "type")
    private long type;
    @Column(name = "consume")
    private long consume;
    @Column(name = "sqlTime")
    private long sqlTime;
    @Column(name = "reportId")
    private String reportId;
    @Column(name = "showTempCostTime")
    private long showTempCostTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getMemory() {
        return memory;
    }

    public void setMemory(double memory) {
        this.memory = memory;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getConsume() {
        return consume;
    }

    public void setConsume(long consume) {
        this.consume = consume;
    }

    public long getSqlTime() {
        return sqlTime;
    }

    public void setSqlTime(long sqlTime) {
        this.sqlTime = sqlTime;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public long getShowTempCostTime() {
        return showTempCostTime;
    }

    public void setShowTempCostTime(long showTempCostTime) {
        this.showTempCostTime = showTempCostTime;
    }
}
