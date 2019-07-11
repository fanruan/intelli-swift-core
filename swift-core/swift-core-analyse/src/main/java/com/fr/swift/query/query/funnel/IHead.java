package com.fr.swift.query.query.funnel;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public interface IHead {

    /**
     * 当前head对应的步骤长度
     *
     * @return
     */
    int getSize();

    String getDate();

    /**
     * 设置新的size大小，重置多余的元素为-1
     * eg: steps = [1, 2, 3, -1, -1], size = 3
     * reset(2) -> steps = [1, 2, -1, -1, -1], size = 2
     *
     * @param size
     */
    void reset(int size);

    void addStep(long timestamp, Object groupValue);

    void setStep(int index, long timestamp, Object groupValue);

    long[] getTimestamps();

    /**
     * 起始步骤的时间戳
     *
     * @return
     */
    long getTimestamp();

    Object getGroupValue();

    void setGroupValue(Object groupValue);

    // 对饮文档的D类：漏斗关联属性
    int getAssociatedProperty();

    void setAssociatedProperty(int associatedProperty);

    IHead copy();

    boolean containsAssociatedEvents(int indexByRow);

    void newEventSet();

    boolean canCopied();

    void setCopied(boolean copied);
}
