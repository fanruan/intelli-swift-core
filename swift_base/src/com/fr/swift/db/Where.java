package com.fr.swift.db;

/**
 * @author anchore
 * @date 2018/3/26
 */
public interface Where {
    /**
     * todo QueryInfo依赖到计算模块啦，考虑更好的替代接口
     *
     * @return query info
     */
    Void toQueryInfo();
}