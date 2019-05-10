package com.fr.swift.cloud.analysis.downtime;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc相关待完事
public abstract class AbstractDowntimeElement implements DowntimeElement {

    protected String appId;
    protected String yearMonth;

    public AbstractDowntimeElement(String appId, String yearMonth) {
        this.appId = appId;
        this.yearMonth = yearMonth;
    }

    public enum ElementType {
        GC, SHUTDOWN, REALTIME_USAGE
    }
}
