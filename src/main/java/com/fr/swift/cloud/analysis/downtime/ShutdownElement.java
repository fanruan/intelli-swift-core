package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.source.Row;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
public class ShutdownElement extends AbstractDowntimeElement {

    private long time;
    private String node;
    private long pid;
    private long startTime;
    private long upTime;
    private String signalName;

    public ShutdownElement(Row row) {
        super(row.getValue(6), row.getValue(7));
        this.time = row.getValue(0);
        this.node = row.getValue(1);
        this.pid = row.getValue(2);
        this.startTime = row.getValue(3);
        this.upTime = row.getValue(4);
        this.signalName = row.getValue(5);
    }

    @Override
    public int pid() {
        return (int) this.pid;
    }

    @Override
    public long recordTime() {
        return time;
    }

    public String getSignalName() {
        return signalName;
    }

    @Override
    public ElementType type() {
        return ElementType.SHUTDOWN;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "time");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "node");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "pid");
        DimensionBean dimensionBean4 = new DimensionBean(DimensionType.DETAIL, "startTime");
        DimensionBean dimensionBean5 = new DimensionBean(DimensionType.DETAIL, "upTime");
        DimensionBean dimensionBean6 = new DimensionBean(DimensionType.DETAIL, "signalName");
        DimensionBean dimensionBean7 = new DimensionBean(DimensionType.DETAIL, "appId");
        DimensionBean dimensionBean8 = new DimensionBean(DimensionType.DETAIL, "yearMonth");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3, dimensionBean4, dimensionBean5, dimensionBean6, dimensionBean7, dimensionBean8};
    }

}
