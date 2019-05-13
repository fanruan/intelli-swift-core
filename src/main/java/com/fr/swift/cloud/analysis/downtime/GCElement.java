package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.source.Row;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public class GCElement extends AbstractDowntimeElement {

    private long gcTime;
    private String gcType;
    private long duration;
    private long twelveHours = 12 * 60 * 60 * 1000L;

    public GCElement(Row row) {
        super(String.valueOf(row.getValue(3)), String.valueOf(row.getValue(4)));
        this.gcTime = row.getValue(0);
        this.gcTime += twelveHours;
        this.gcType = row.getValue(1);
        this.duration = row.getValue(2);
    }

    @Override
    public int pid() {
        return -1;
    }

    @Override
    public long recordTime() {
        return gcTime;
    }

    public String getGcType() {
        return gcType;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public ElementType type() {
        return ElementType.GC;
    }
}
