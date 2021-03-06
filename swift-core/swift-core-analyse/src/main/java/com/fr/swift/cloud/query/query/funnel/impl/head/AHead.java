package com.fr.swift.cloud.query.query.funnel.impl.head;

import com.fr.swift.cloud.query.query.funnel.IHead;

import java.io.Serializable;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class AHead implements IHead, Serializable {

    private static final long serialVersionUID = -5646429773979005076L;
    private int size;
    private String date;
    private long[] timestamps;
    private int associatedProperty = -1;
    private boolean canCopied = true;
    //    private Object groupValue;
    private Object[] groupValue;

    /**
     * 漏斗起始事件对应的步骤
     *
     * @param numberOfSteps      漏斗总共定义了多少个步骤
     * @param associatedProperty 关联的属性值
     */
    public AHead(int numberOfSteps, String date, int associatedProperty) {
        this.size = 0;
        this.date = date;
        this.associatedProperty = associatedProperty;
        this.timestamps = new long[numberOfSteps];
        this.groupValue = new Object[numberOfSteps];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public void reset(int size) {
    }

    @Override
    public void addStep(long timestamp, Object groupValue) {
        timestamps[size] = timestamp;
        this.groupValue[size] = groupValue;
        size++;
    }

    @Override
    public void setStep(int index, long timeStamp, Object groupValue) {
        canCopied = true;
        timestamps[index] = timeStamp;
        this.groupValue[index] = groupValue;
    }

    @Override
    public long[] getTimestamps() {
        return timestamps;
    }

    @Override
    public long getTimestamp() {
        return timestamps[0];
    }

    @Override
    public Object getGroupValue() {
        for (Object value : groupValue) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void setGroupValue(Object groupValue) {
//        this.groupValue = groupValue;
    }

    @Override
    public int getAssociatedProperty() {
        return associatedProperty;
    }

    @Override
    public void setAssociatedProperty(int associatedProperty) {
        this.associatedProperty = associatedProperty;
    }

    public boolean containsAssociatedEvents(int associatedProperty) {
        return false;
    }

    @Override
    public void newEventSet() {
    }

    @Override
    public IHead copy() {
        canCopied = false;
        IHead head = new AHead(timestamps.length, date, associatedProperty);
        for (int i = 0; i < size; i++) {
            head.addStep(timestamps[i], groupValue[i]);
        }
        copyCounter++;
        return head;
    }

    public static int copyCounter = 0;

    @Override
    public boolean canCopied() {
        return canCopied;
    }

    @Override
    public void setCopied(boolean copied) {
        this.canCopied = copied;
    }
}