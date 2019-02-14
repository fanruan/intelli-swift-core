package com.fr.swift.query.session.factory;

import com.fr.swift.query.session.Session;
import com.fr.swift.util.Clearable;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface SessionFactory extends Clearable {
    /**
     * 打开一个session
     *
     * @param queryId queryId
     * @return
     */
    Session openSession(String queryId);

    /**
     * 设置缓存超时时间 默认5分钟
     *
     * @param cacheTimeout
     */
    void setCacheTimeout(long cacheTimeout);

    /**
     * 设置session超时时间 默认10分钟
     *
     * @param sessionTimeout
     */
    void setSessionTimeout(long sessionTimeout);

    /**
     * 设置最大闲置session数
     * 暂时不知道maxIdle怎么整先留着
     * TODO 最大闲置session怎么整呢
     *
     * @param maxIdle
     */
    void setMaxIdle(int maxIdle);
}
