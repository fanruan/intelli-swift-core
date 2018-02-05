package com.fr.swift.source;

/**
 * Created by pony on 2017/10/25.
 * 根据datasource行式获取数据的接口
 */
public interface SwiftSourceTransfer {
    SwiftResultSet EMPTY = new SwiftResultSet() {
        @Override
        public void close() {

        }

        @Override
        public SwiftMetaData getMetaData() {
            return null;
        }

        @Override
        public Row getRowData() {
            return null;
        }

        @Override
        public boolean next() {
            return false;
        }

    };

    /**
     * 类似result set的
     * @return
     */
    SwiftResultSet createResultSet();
}
