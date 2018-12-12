package com.fr.swift.source;

import com.fr.swift.result.SwiftResultSet;

/**
 * @author pony
 * @date 2017/10/25
 * 根据datasource行式获取数据的接口
 */
public interface SwiftSourceTransfer {
    SwiftResultSet EMPTY = new SwiftResultSet() {
        @Override
        public void close() {
        }

        @Override
        public int getFetchSize() {
            return 0;
        }

        @Override
        public SwiftMetaData getMetaData() {
            return null;
        }

        @Override
        public Row getNextRow() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }
    };

    /**
     * 类似result set的
     *
     * @return
     */
    SwiftResultSet createResultSet();
}
