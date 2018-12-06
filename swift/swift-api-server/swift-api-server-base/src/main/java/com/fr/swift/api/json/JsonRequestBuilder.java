package com.fr.swift.api.json;


import com.fr.swift.api.info.RequestInfo;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface JsonRequestBuilder {
//    JsonRequestBuilder BUILDER = new JdbcJsonRequestBuilder();

    /**
     * build request json info
     *
     * @param requestInfo
     * @return
     */
    String buildRequest(RequestInfo requestInfo);
}
