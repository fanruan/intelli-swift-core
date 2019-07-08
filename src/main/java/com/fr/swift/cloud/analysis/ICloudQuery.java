package com.fr.swift.cloud.analysis;

/**
 * Create by lifan on 2019-07-05 09:57
 */
public interface ICloudQuery {


    void calculate(String appId, String yearMonth) throws Exception;

    String getTableName();

}

