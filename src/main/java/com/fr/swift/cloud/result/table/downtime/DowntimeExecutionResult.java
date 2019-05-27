package com.fr.swift.cloud.result.table.downtime;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.Row;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * This class created on 2019/5/24
 *
 * @author Lucifer
 * @description
 */
@Entity
@Table(name = "downtime_execution_result")
public class DowntimeExecutionResult implements Serializable {

    private static final long serialVersionUID = 4316633984923026373L;
    @Id
    private String id;
    @Id
    @Column(name = "relationId")
    private String relationId;

    @Column(name = "tName")
    private String tName;
    @Column(name = "time")
    private long time;
    @Column(name = "consume")
    private long consume;
    @Column(name = "sqlTime")
    private long sqlTime;
    @Column(name = "memory")
    private long memory;
    @Column(name = "reportId")
    private String reportId;
    @Column(name = "complete")
    private String complete;
    @Column(name = "data")
    private long data;
    @Column(name = "appId")
    private String appId;

    @Column(name = "yearMonth")
    private Date yearMonth;

    public DowntimeExecutionResult(Row row, String relationId, String appId, Date yearMonth) {
        this.id = row.getValue(0);
        this.tName = row.getValue(1);
        this.time = row.getValue(2);
        this.consume = row.getValue(3);
        this.sqlTime = row.getValue(4);
        this.memory = row.getValue(5);
        this.reportId = row.getValue(6);
        this.complete = row.getValue(7);
        this.relationId = relationId;
        this.appId = appId;
        this.yearMonth = yearMonth;
    }

    public String getId() {
        return id;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public void setData(long data) {
        this.data = data;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "id");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "tName");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "time");
        DimensionBean dimensionBean4 = new DimensionBean(DimensionType.DETAIL, "consume");
        DimensionBean dimensionBean5 = new DimensionBean(DimensionType.DETAIL, "sqlTime");
        DimensionBean dimensionBean6 = new DimensionBean(DimensionType.DETAIL, "memory");
        DimensionBean dimensionBean7 = new DimensionBean(DimensionType.DETAIL, "reportId");
        DimensionBean dimensionBean8 = new DimensionBean(DimensionType.DETAIL, "complete");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3, dimensionBean4, dimensionBean5, dimensionBean6, dimensionBean7, dimensionBean8};
    }
}
