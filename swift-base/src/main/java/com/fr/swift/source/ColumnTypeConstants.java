package com.fr.swift.source;

import java.io.Serializable;

/**
 * @author 小灰灰
 * @date 2015/9/7
 */
public class ColumnTypeConstants implements Serializable {
    private static final long serialVersionUID = -2329388093367371957L;

    /**
     * 字段类型
     * 对外展示的类型
     *
     * @author frank
     */
    public enum ColumnType {
        //
        NUMBER, STRING, DATE
    }

    /**
     * Java对象类型
     * 顺序不能变哦，Double>Long>Integer
     * @author frank
     */
    public enum ClassType {
        //
        INTEGER, LONG, DOUBLE, DATE, STRING
    }
}