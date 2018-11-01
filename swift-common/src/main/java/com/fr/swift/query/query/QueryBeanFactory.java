package com.fr.swift.query.query;

import java.io.IOException;
import java.net.URL;

/**
 * @author yee
 * @date 2018/8/7
 */
public interface QueryBeanFactory {
    <T extends QueryBean> T create(URL url) throws IOException;

    /**
     * @param json        字符串
     * @param initQueryId 是否初始化queryId外。只有新建查询的时候需要初始化queryId
     * @param <T>
     * @return
     * @throws IOException
     */
    <T extends QueryBean> T create(String json, boolean initQueryId) throws IOException;
}
