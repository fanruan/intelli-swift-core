package com.fr.swift.source;

/**
 * Created by 小灰灰 on 2015/9/7.
 */
public class ColumnTypeConstants {

    /**
     * 字段类型
     *
     * @author frank
     */
    public static final class COLUMN {
        public static final int NUMBER = 0x20;

        public static final int STRING = 0x10;

        public static final int DATE = 0x30;

    }

    /**
     * 字段类型 对外展示的类型
     *
     * @author frank
     */
    public enum ColumnType {
        NUMBER, STRING, DATE
    }

    /**
     * Java对象类型
     *
     * @author frank
     */
    public enum ClassType {
        INTEGER, LONG, DOUBLE, DATE, STRING
    }
}