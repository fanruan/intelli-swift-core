package com.fr.swift.query.aggregator.funnel.impl;


import com.fr.swift.query.aggregator.funnel.IHead;

import java.io.Serializable;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class Head implements IHead, Serializable {

    private static final long serialVersionUID = -5646429773979005076L;
    private int size;
    private int date;
    private int[] timestamps;
    private int associatedProperty = -1;
    private boolean[] associatedMap;
    //    private Object groupValue;
    private Object[] groupValue;

    /**
     * 漏斗起始事件对应的步骤
     *
     * @param numberOfSteps      漏斗总共定义了多少个步骤
     * @param associatedProperty 关联的属性值
     */
    public Head(int numberOfSteps, int date, int associatedProperty, int associatedColumnSize) {
        this.size = 0;
        this.date = date;
        this.associatedProperty = associatedProperty;
        this.timestamps = new int[numberOfSteps];
        this.groupValue = new Object[numberOfSteps];
        this.associatedMap = new boolean[associatedColumnSize];
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getDate() {
        return date;
    }

    @Override
    public void reset(int size) {
        this.size = size;
        for (int i = size; i < timestamps.length; i++) {
            timestamps[i] = 0;
        }
    }

    @Override
    public void addStep(int timestamp, Object groupValue) {
        timestamps[size] = timestamp;
        this.groupValue[size] = groupValue;
        size++;
    }

    @Override
    public void setStep(int index, int timeStamp, Object groupValue) {
        timestamps[index] = timeStamp;
        this.groupValue[index] = groupValue;
    }

    @Override
    public int[] getTimestamps() {
        return timestamps;
    }

    @Override
    public int getTimestamp() {
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
        if (associatedProperty != -1) {
            associatedMap[associatedProperty] = true;
        }
    }

    public boolean containsAssociatedEvents(int associatedProperty) {
        return associatedMap[associatedProperty];
    }

    @Override
    public void newEventSet() {
        associatedMap = new boolean[associatedMap.length];
    }

    @Override
    public IHead copy() {
        IHead head = new Head(timestamps.length, date, associatedProperty, associatedMap.length);
        ((Head) head).associatedMap = associatedMap;
        for (int i = 0; i < size; i++) {
            head.addStep(timestamps[i], groupValue[i]);
        }
        copyCounter++;
        return head;
    }

    public static int copyCounter = 0;

    @Override
    public boolean canCopied() {
        return false;
    }

    @Override
    public void setCopied(boolean copied) {

    }
}