package com.fr.swift.cloud.result.table;

import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.Row;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * This class created on 2019/6/12
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "system_usage_info")
public class SystemUsageInfo implements Serializable {

    public transient static final String tableName = "system_usage_info";
    private static final long serialVersionUID = 689121835735718268L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "appId")
    private String appId;
    @Column(name = "yearMonth")
    private Date yearMonth;
    @Column(name = "node")
    private String node;

    @Column(name = "avgCpu")
    private double avgCpu;
    @Column(name = "avgMem")
    private double avgMem;
    @Column(name = "avgOnline")
    private double avgOnline;
    @Column(name = "avgSession")
    private double avgSession;

    @Column(name = "maxCpu")
    private double maxCpu;
    @Column(name = "maxMem")
    private double maxMem;
    @Column(name = "maxOnline")
    private double maxOnline;
    @Column(name = "maxSession")
    private double maxSession;

    @Column(name = "downTime")
    private int downTime;
    @Column(name = "stopTime")
    private int stopTime;

    public SystemUsageInfo(Row row, String appId, String yearMonth, int downTime, int stopTime) {
        if(row != null) {
            this.node = row.getValue(0);
            this.avgCpu = row.getValue(1);
            this.avgMem = row.getValue(2);
            this.avgOnline = row.getValue(3) == null ? 0 : row.getValue(3);
            this.avgSession = row.getValue(4) == null ? 0 : row.getValue(4);
            this.maxCpu = row.getValue(5);
            this.maxMem = row.getValue(6);
            this.maxOnline = row.getValue(7) == null ? 0 : row.getValue(7);
            this.maxSession = row.getValue(8) == null ? 0 : row.getValue(8);
        }
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
        this.downTime = downTime;
        this.stopTime = stopTime;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "node");
        return new DimensionBean[]{dimensionBean1};
    }

    public static MetricBean[] getAggregations() {
        MetricBean avgCpuBean = new MetricBean("cpu", AggregatorType.AVERAGE);
        MetricBean avgMemBean = new MetricBean("memory", AggregatorType.AVERAGE);
        MetricBean avgOnlineBean = new MetricBean("onlinenum", AggregatorType.AVERAGE);
        MetricBean avgSessionBean = new MetricBean("sessionnum", AggregatorType.AVERAGE);
        MetricBean maxCpuBean = new MetricBean("cpu", AggregatorType.MAX);
        MetricBean maxMemBean = new MetricBean("memory", AggregatorType.MAX);
        MetricBean maxOnlineBean = new MetricBean("onlinenum", AggregatorType.MAX);
        MetricBean maxSessionBean = new MetricBean("sessionnum", AggregatorType.MAX);

        return new MetricBean[]{avgCpuBean, avgMemBean, avgOnlineBean, avgSessionBean, maxCpuBean, maxMemBean, maxOnlineBean, maxSessionBean};
    }
}
