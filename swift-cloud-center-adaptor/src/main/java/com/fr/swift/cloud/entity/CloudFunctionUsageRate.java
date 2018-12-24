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
@Table(name = "cloud_func_usage_rate")
public class CloudFunctionUsageRate {
    @Column
    private String id;
    @Column
    private Date time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
