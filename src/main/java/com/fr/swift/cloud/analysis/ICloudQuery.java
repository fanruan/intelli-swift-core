package com.fr.swift.cloud.analysis;

/**
 * Create by lifan on 2019-07-05 09:57
 */
public interface ICloudQuery {

    /**
     * 查询并计算表数据 并写入数据库
     *
     * @param appId
     * @param yearMonth
     * @throws Exception
     */
    void calculate(String appId, String yearMonth) throws Exception;

    /**
     * 获取对应表名字
     *
     * @return 对应的表名字
     */
    String getTableName();

}

