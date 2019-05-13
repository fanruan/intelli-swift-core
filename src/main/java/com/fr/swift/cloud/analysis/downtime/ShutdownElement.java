package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.source.Row;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public class ShutdownElement extends AbstractDowntimeElement {

    private long time;
    private String node;
    private long pid;
    private long startTime;
    private long upTime;
    private String signalName;

    public ShutdownElement(Row row) {
        super(String.valueOf(row.getValue(6)), String.valueOf(row.getValue(7)));
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

    public long getStartTime() {
        return startTime;
    }

    public long getUpTime() {
        return upTime;
    }

    public String getSignalName() {
        return signalName;
    }

    @Override
    public ElementType type() {
        return ElementType.SHUTDOWN;
    }
}
