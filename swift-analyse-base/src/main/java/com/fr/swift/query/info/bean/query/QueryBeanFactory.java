package com.fr.swift.query.info.bean.query;

import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.query.query.QueryBean;

/**
 * @author yee
 * @date 2018/8/7
 */
public class QueryBeanFactory {

    public static String queryBean2String(QueryBean bean) throws Exception {
        return JsonBuilder.writeJsonString(bean);
    }

    public static QueryInfoBean create(String jsonString) throws Exception {
        return JsonBuilder.readValue(jsonString, QueryInfoBean.class);
    }
}
