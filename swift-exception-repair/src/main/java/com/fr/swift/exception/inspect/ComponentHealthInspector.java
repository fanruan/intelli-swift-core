package com.fr.swift.exception.inspect;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 8/30/2019
 */
public interface ComponentHealthInspector<R, T> {
    /**
     * 检测组件是否可用
     *
     * @param info
     * @return
     */
    R inspect(T info);
}