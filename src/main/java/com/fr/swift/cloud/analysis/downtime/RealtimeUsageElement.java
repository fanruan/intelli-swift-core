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
public class RealtimeUsageElement extends AbstractDowntimeElement {

    private long time;
    private String node;
    private double cpu;
    private long memory;
    private long sessionNum;
    private long onlineNum;
    private long pid;

    public RealtimeUsageElement(Row row) {
        super(row.getValue(7), row.getValue(8));
        this.time = row.getValue(0);
        this.node = row.getValue(1);
        this.cpu = row.getValue(2);
        this.memory = row.getValue(3);
        this.sessionNum = row.getValue(4);
        this.onlineNum = row.getValue(5);
        this.pid = row.getValue(6);

    }

    public String node() {
        return node;
    }

    @Override
    public int pid() {
        return (int) this.pid;
    }

    public double cpu() {
        return cpu;
    }

    public long memory() {
        return memory;
    }

    @Override
    public long recordTime() {
        return time;
    }

    @Override
    public ElementType type() {
        return ElementType.REALTIME_USAGE;
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "time");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "node");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "cpu");
        DimensionBean dimensionBean4 = new DimensionBean(DimensionType.DETAIL, "memory");
        DimensionBean dimensionBean5 = new DimensionBean(DimensionType.DETAIL, "sessionnum");
        DimensionBean dimensionBean6 = new DimensionBean(DimensionType.DETAIL, "onlinenum");
        DimensionBean dimensionBean7 = new DimensionBean(DimensionType.DETAIL, "pid");
        DimensionBean dimensionBean8 = new DimensionBean(DimensionType.DETAIL, "appId");
        DimensionBean dimensionBean9 = new DimensionBean(DimensionType.DETAIL, "yearMonth");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3, dimensionBean4, dimensionBean5, dimensionBean6, dimensionBean7, dimensionBean8, dimensionBean9};
    }
}
