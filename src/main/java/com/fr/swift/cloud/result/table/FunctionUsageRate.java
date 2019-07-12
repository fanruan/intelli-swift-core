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
@Table(name = "function_usage_rate")
public class FunctionUsageRate implements Serializable {

    public transient static final String tableName = "function_usage_rate";
    private static final long serialVersionUID = 4587562261059132038L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int keyId;

    @Column(name = "yearMonth")
    private Date yearMonth;

    @Column(name = "appId")
    private String appId;

    @Column(name = "id")
    private String id;

    @Column(name = "source")
    private String source;

    @Column(name = "text")
    private String text;

    @Column(name = "body")
    private String body;

    @Column(name = "number")
    private double number;

    public FunctionUsageRate(Row row, String appId, String yearMonth) {
        this.id = row.getValue(0);
        this.source = row.getValue(1);
        this.text = row.getValue(2);
        this.body = row.getValue(3);
        this.number = row.getValue(4);
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimension1 = new DimensionBean(DimensionType.GROUP, "id");
        DimensionBean dimension2 = new DimensionBean(DimensionType.GROUP, "source");
        DimensionBean dimension3 = new DimensionBean(DimensionType.GROUP, "text");
        DimensionBean dimension4 = new DimensionBean(DimensionType.GROUP, "body");
        return new DimensionBean[]{dimension1, dimension2, dimension3, dimension4};
    }

    public static MetricBean[] getAggregations() {
        MetricBean metricBean = new MetricBean("id", AggregatorType.COUNT);
        return new MetricBean[]{metricBean};
    }
}