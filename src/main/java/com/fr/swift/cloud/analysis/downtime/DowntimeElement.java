package com.fr.swift.cloud.analysis.downtime;

/**
 * This class created on 2019/4/28
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc demo待重构
public interface DowntimeElement {

    int pid();

    long recordTime();

    AbstractDowntimeElement.ElementType type();
}
