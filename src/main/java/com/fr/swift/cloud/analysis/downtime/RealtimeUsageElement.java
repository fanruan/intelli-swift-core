package com.fr.swift.cloud.analysis.downtime;

import com.fr.swift.source.Row;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public class RealtimeUsageElement extends AbstractDowntimeElement {

    private long time;
    private String node;
    private double cpu;
    private long memory;
    private long sessionNum;
    private long onlineNum;
    private long pid;

    public RealtimeUsageElement(Row row) {
        super(String.valueOf(row.getValue(7)), String.valueOf(row.getValue(8)));
        this.time = row.getValue(0);
        this.node = row.getValue(1);
        this.cpu = row.getValue(2);
        this.memory = row.getValue(3);
        this.sessionNum = row.getValue(4);
        this.onlineNum = row.getValue(5);
        this.pid = row.getValue(6);

    }

    @Override
    public int pid() {
        return (int) this.pid;
    }

    public double getCpu() {
        return cpu;
    }

    public long getMemory() {
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
}
