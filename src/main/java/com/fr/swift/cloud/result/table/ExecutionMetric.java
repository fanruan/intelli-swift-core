package com.fr.swift.cloud.result.table;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lyon on 2019/3/3.
 */
@Entity
@Table(name = "execution_metric")
public class ExecutionMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tName;

    /**
     * 模板执行相关指标统计值
     */
    @Column
    private long consume;
    @Column
    private long sqlTime;
    @Column
    private long count;

    /**
     * 代表客户id
     */
    @Column
    private String appId;
    /**
     * 数据所在月份
     */
    @Column
    private Date yearMonth;

    public ExecutionMetric(String tName, long[] values, String appId, Date yearMonth) {
        this.tName = tName;
        this.consume = values[0];
        this.sqlTime = values[1];
        this.count = values[2];
        this.appId = appId;
        this.yearMonth = yearMonth;
    }
}
