package com.fr.swift.cloud.result.table.template;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lyon on 2019/2/28.
 */
@Entity
@Table(name = "latency_top_percentile_statistic")
public class LatencyTopPercentileStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String appId;
    @Column
    private Date yearMonth;
    @Column
    private int percentile;
    @Column
    private long latency;

    public LatencyTopPercentileStatistic() {
    }

    public LatencyTopPercentileStatistic(String appId, Date yearMonth, int percentile, long latency) {
        this.appId = appId;
        this.yearMonth = yearMonth;
        this.percentile = percentile;
        this.latency = latency;
    }
}
