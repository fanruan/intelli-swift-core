package com.fr.swift.query.info.bean.query;

import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Lyon on 2018/6/7.
 */
public class QueryBeanFactory {

    private static ObjectMapper MAPPER = new ObjectMapper();

    public static QueryBean create(URL url) throws IOException {
        return MAPPER.readValue(url, QueryBean.class);
    }
}
