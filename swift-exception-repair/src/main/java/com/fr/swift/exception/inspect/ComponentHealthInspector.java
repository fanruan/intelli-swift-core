package com.fr.swift.exception.inspect;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public interface ComponentHealthInspector<T, E> {
    /**
     * 检测组件是否可用
     *
     * @param inspectedObject
     * @return
     */
    T inspect(E inspectedObject);
}