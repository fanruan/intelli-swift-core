package com.fr.swift.result;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @Author: Bellman
 * @Date: 2019/9/23 3:06 下午
 */
public class SlimMutableResultSet implements MutableResultSet {
    private SwiftMetaData metadata;

    private SwiftResultSet swiftResultSet;

    public SlimMutableResultSet(SwiftMetaData metadata, SwiftResultSet swiftResultSet) {
        this.metadata = metadata;
        this.swiftResultSet = swiftResultSet;
    }

    @Override
    public boolean hasNewSubfields() {
        return false;
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    /**
     * meta
     * 这里只用负责现有字段的导入，json 解析交给子表
     *
     * @return meta
     */
    @Override
    public SwiftMetaData getMetaData() {
        return this.metadata;
    }

    /**
     * if has next
     *
     * @return 是否有下一个
     * @throws SQLException 异常
     */
    @Override
    public boolean hasNext() throws SQLException {
        return this.swiftResultSet.hasNext();
    }

    /**
     * get row
     *
     * @return row
     * @throws SQLException 异常
     */
    @Override
    public Row getNextRow() throws SQLException {
        return this.swiftResultSet.getNextRow();
    }

    /**
     * 关闭
     *
     * @throws SQLException 异常
     */
    @Override
    public void close() throws SQLException {
        this.swiftResultSet.close();
    }
}
