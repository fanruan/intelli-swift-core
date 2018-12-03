package com.fr.swift.jdbc.json;

import com.fr.swift.jdbc.info.RequestInfo;
import com.fr.swift.jdbc.json.impl.JdbcJsonRequestBuilder;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface JsonRequestBuilder {
    JsonRequestBuilder BUILDER = new JdbcJsonRequestBuilder();

    /**
     * build request json info
     *
     * @param requestInfo
     * @return
     */
    String buildRequest(RequestInfo requestInfo);
}
