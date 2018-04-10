package com.fr.swift.db;

import com.fr.swift.source.Source;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Table extends Source {
    /**
     * 元数据
     *
     * @return meta
     * @throws SQLException 异常
     */
    SwiftMetaData getMeta() throws SQLException;

    /**
     * 设置meta
     * todo 考虑下更好的替代接口
     *
     * @param meta 新的元数据
     * @throws SQLException 异常
     */
    void setMeta(SwiftMetaData meta) throws SQLException;

    /**
     * 增：插入
     *
     * @param rowSet 结果集
     * @throws SQLException 异常
     */
    void insert(SwiftResultSet rowSet) throws SQLException;

    /**
     * 增：导入
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
}