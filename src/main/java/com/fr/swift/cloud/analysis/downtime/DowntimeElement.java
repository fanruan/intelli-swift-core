package com.fr.swift.cloud.analysis.downtime;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
public interface DowntimeElement {

    int pid();

    long recordTime();

    AbstractDowntimeElement.ElementType type();
}
