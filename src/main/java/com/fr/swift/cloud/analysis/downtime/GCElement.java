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
public class GCElement extends AbstractDowntimeElement {

    private GcType gcType;
    private long startTime;
    private long endTime;
    private long duration;
    private long pid;

    public GCElement(Row row) {
        super(row.getValue(5), row.getValue(6));
        this.startTime = row.getValue(0);
        this.endTime = row.getValue(1);
        this.gcType = GcType.getGcType(row.getValue(2));
        this.duration = row.getValue(3);
        this.pid = row.getValue(4);
    }

    @Override
    public int pid() {
        return (int) pid;
    }

    @Override
    public long recordTime() {
        return startTime;
    }

    public long duration() {
        return duration;
    }

    @Override
    public ElementType type() {
        return ElementType.GC;
    }

    public enum GcType {
        MAJOR_GC("major GC"), MINOR_GC("minor GC");

        private String gcType;

        GcType(String gcType) {
            this.gcType = gcType;
        }

        public static GcType getGcType(String gcType) {
            for (GcType value : values()) {
                if (value.gcType.equals(gcType)) {
                    return value;
                }
            }
            return null;
        }
    }

    public static DimensionBean[] getDimensions() {
        DimensionBean dimensionBean1 = new DimensionBean(DimensionType.DETAIL, "gcStartTime");
        DimensionBean dimensionBean2 = new DimensionBean(DimensionType.DETAIL, "gcEndTime");
        DimensionBean dimensionBean3 = new DimensionBean(DimensionType.DETAIL, "gcType");
        DimensionBean dimensionBean4 = new DimensionBean(DimensionType.DETAIL, "duration");
        DimensionBean dimensionBean5 = new DimensionBean(DimensionType.DETAIL, "pid");
        DimensionBean dimensionBean6 = new DimensionBean(DimensionType.DETAIL, "appId");
        DimensionBean dimensionBean7 = new DimensionBean(DimensionType.DETAIL, "yearMonth");
        return new DimensionBean[]{dimensionBean1, dimensionBean2, dimensionBean3, dimensionBean4, dimensionBean5, dimensionBean6, dimensionBean7};
    }
}
