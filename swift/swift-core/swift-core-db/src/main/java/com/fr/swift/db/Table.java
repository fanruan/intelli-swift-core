package com.fr.swift.db;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/3/26
 * <p>
 * todo 讨论：这个接口是否太顶层了，比如导入，查询都要依赖底层，考虑去除？
 */
public interface Table extends Source, DataSource {
    /**
     * 元数据
     *
     * @return meta
     * @throws SQLException 异常
     */
    SwiftMetaData getMeta() throws SQLException;

    /**
     * 增：插入
     * 加到内存，达到一定量进行合并写入
     *
     * @param rowSet 结果集
     * @throws SQLException 异常
     */
    void insert(SwiftResultSet rowSet) throws SQLException;

    /**
     * 增：导入
     * 直接写入，适用于大量数据
     *
     * @param rowSet 结果及
     * @throws SQLException 异常
     */
    void importFrom(SwiftResultSet rowSet) throws SQLException;

    /**
     * 删
     *
     * @param where 条件
     * @return 删除条数
     * @throws SQLException 异常
     */
    int delete(Where where) throws SQLException;

    /**
     * 改
     *
     * @param where  条件
     * @param rowSet 改的结果集
     * @return 修改条数
     * @throws SQLException 异常
     */
    int update(Where where, SwiftResultSet rowSet) throws SQLException;

    /**
     * 查
     *
     * @param where 条件
     * @return 结果集
     * @throws SQLException 异常
     */
    SwiftResultSet select(Where where) throws SQLException;

    @Override
    String toString();
}