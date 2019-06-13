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
@Table(name = "plugin_usage")
public class PluginUsage implements Serializable {

    private static final long serialVersionUID = -5721979817381089048L;
    public transient static final String tableName = "plugin_usage";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "appId")
    private String appId;
    @Column(name = "plugin")
    private String plugin;
    @Column(name = "version")
    private String version;
    @Column(name = "API")
    private String API;
    @Column(name = "operation")
    private String operation;
    @Column(name = "register")
    private String register;
    @Column(name = "enable")
    private long enable;
    @Column(name = "time")
    private long time;
    @Column(name = "yearMonth")
    private Date yearMonth;

    public PluginUsage(Row row, String appId, String yearMonth) {
        this.plugin = row.getValue(0);
        this.version = row.getValue(1);
        this.API = row.getValue(2);
        this.operation = row.getValue(3);
        this.register = row.getValue(4);
        this.enable = row.getValue(5);
        this.time = row.getValue(6);
        this.appId = appId;
        this.yearMonth = TimeUtils.yearMonth2Date(yearMonth);
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "plugin");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "version");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "API");
        DimensionBean dimensionBean4 = new DimensionBean(DimensionType.DETAIL, "operation");
        DimensionBean dimensionBean5 = new DimensionBean(DimensionType.DETAIL, "register");
        DimensionBean dimensionBean6 = new DimensionBean(DimensionType.DETAIL, "enable");
        DimensionBean dimensionBean7 = new DimensionBean(DimensionType.DETAIL, "time");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3, dimensionBean4,
                dimensionBean5, dimensionBean6, dimensionBean7};
    }
}
