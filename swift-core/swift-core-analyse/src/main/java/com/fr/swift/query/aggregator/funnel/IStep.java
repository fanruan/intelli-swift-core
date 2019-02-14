package com.fr.swift.query.aggregator.funnel;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface IStep {

    /**
     * @param eventIndex 第几个步骤
     * @param event      事件全局字典序号
     * @return
     */
    boolean isEqual(int eventIndex, int event);

    int size();

    boolean isHeadRepeated();

    boolean hasRepeatedEvents();

    IStep toNoRepeatedStep();

    int getEventIndex(int event);
}
