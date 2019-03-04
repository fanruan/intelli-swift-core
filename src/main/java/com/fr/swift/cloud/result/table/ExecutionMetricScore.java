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
@Table(name = "execution_metric_score")
public class ExecutionMetricScore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String tName;

    /**
     * 模板指标统计值按一定规则换算成百分制的值
     */
    @Column
    private long consumeScore;
    @Column
    private long sqlTimeScore;
    @Column
    private long coreConsumeScore;
    @Column
    private long memoryScore;
    @Column
    private long countScore;

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

    public ExecutionMetricScore(String tName, long[] values, String appId, Date yearMonth) {
        this.tName = tName;
        this.consumeScore = values[0];
        this.sqlTimeScore = values[1];
        this.coreConsumeScore = values[2];
        this.memoryScore = values[3];
        this.countScore = values[4];
        this.appId = appId;
        this.yearMonth = yearMonth;
    }
}
