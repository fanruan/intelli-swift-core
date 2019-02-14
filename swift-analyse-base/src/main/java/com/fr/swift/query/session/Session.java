package com.fr.swift.query.session;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.result.qrs.QueryResultSet;

import java.io.Closeable;
import java.sql.SQLException;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface Session extends Closeable {

    /**
     * 取的sessionId
     *
     * @return
     */
    String getSessionId();

    /**
     * 查询当前节点
     *
     * @param queryInfo
     * @return
     * @throws SQLException
     */
    QueryResultSet executeQuery(QueryBean queryInfo) throws Exception;

    /**
     * 关闭并清理缓存
     */
    @Override
    void close();

    /**
     * session是否关闭
     *
     * @return
     */
    boolean isClose();

    /**
     * 清理缓存
     *
     * @param force true 无论怎样都清理 false 超时的清理
     */
    void cleanCache(boolean force);

    void putObject(Object key, Object value);

    Object getObject(Object key);

    void removeObject(Object key);
}
