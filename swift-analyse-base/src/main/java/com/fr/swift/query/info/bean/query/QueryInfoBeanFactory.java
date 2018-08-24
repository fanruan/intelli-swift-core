package com.fr.swift.query.info.bean.query;

import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.query.QueryBeanFactory;
import com.fr.third.fasterxml.jackson.core.JsonProcessingException;
import com.fr.third.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;

/**
 * @author Lyon
 * @date 2018/6/7
 */
public class QueryInfoBeanFactory implements QueryBeanFactory {

    private static ObjectMapper MAPPER = new ObjectMapper();

    public static String queryBean2String(QueryBean bean) throws JsonProcessingException {
        return MAPPER.writeValueAsString(bean);
    }

    @Override
    public QueryInfoBean create(URL url) throws IOException {
        return MAPPER.readValue(url, QueryInfoBean.class);
    }

    @Override
    public QueryInfoBean create(String jsonString) throws IOException {
        return MAPPER.readValue(jsonString, QueryInfoBean.class);
    }

}
