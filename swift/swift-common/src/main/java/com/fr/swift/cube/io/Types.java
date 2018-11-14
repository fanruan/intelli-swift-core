package com.fr.swift.cube.io;

/**
 * @author anchore
 * @date 2017/11/24
 */

public final class Types {
    public enum IoType {
        /**
         * 读写类型
         */
        READ, WRITE
    }

    public enum DataType {
        /**
         * 数据类型
         */
        BYTE, INT, LONG, DOUBLE,
        BYTE_ARRAY, STRING, BITMAP, LONG_ARRAY,
        REALTIME_COLUMN
    }

    public enum WriteType {
        /**
         * 覆写
         */
        OVERWRITE,
        // 追加
        APPEND
    }

    public enum StoreType {
        /**
         * 存储类型
         */
        FINE_IO, MEMORY, NIO;

        /**
         * 持久化的
         * @return 是否为持久化的
         */
        public boolean isPersistent() {
            return this != MEMORY;
        }

        /**
         * 未持久化的，如内存化的
         * @return 是否为未持久化的
         */
        public boolean isTransient() {
            return this == MEMORY;
        }
    }

}