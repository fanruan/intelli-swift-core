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
    void queryAndSave(String appId, String yearMonth) throws Exception;
}