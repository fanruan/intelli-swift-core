package com.fr.swift.query.query;

import java.io.IOException;
import java.net.URL;

/**
 * @author yee
 * @date 2018/8/7
 */
public interface QueryBeanFactory {
    <T extends QueryBean> T create(URL url) throws IOException;

    <T extends QueryBean> T create(String json) throws IOException;
}
