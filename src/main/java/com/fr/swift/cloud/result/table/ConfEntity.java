package com.fr.swift.cloud.result.table;

import com.fr.swift.cloud.util.TimeUtils;
import com.fr.swift.query.info.bean.element.DimensionBean;
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
@Table(name = "conf_entity")
public class ConfEntity implements Serializable {

    public transient static final String tableName = "conf_entity";
    private static final long serialVersionUID = -2035360549437965666L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int keyId;

    @Column(name = "appId")
    private String appId;

    @Column(name = "id")
    private String id;

    @Column(name = "value")
    private String value;

    @Column(name = "time")
    private long time;

    @Column(name = "yearMonth")
    private Date yearMonth;

    public ConfEntity(Row row, String appId, String yearMonth) {
        this.id = row.getValue(0);
        this.value = row.getValue(1);
        this.time = row.getValue(2);
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "id");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "value");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "time");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3};
    }
}