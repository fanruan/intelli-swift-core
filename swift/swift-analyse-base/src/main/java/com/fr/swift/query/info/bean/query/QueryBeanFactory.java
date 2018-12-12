//package com.fr.swift.query.info.bean.query;
//
//import com.fr.swift.base.json.core.JsonProcessingException;
//import com.fr.swift.base.json.databind.ObjectMapper;
//import com.fr.swift.query.query.QueryBean;
//
//import java.io.IOException;
//import java.net.URL;
//
///**
// * @author yee
// * @date 2018/8/7
// */
//public class QueryBeanFactory {
//    private static ObjectMapper MAPPER = new ObjectMapper();
//
//    public static String queryBean2String(QueryBean bean) throws JsonProcessingException {
//        return MAPPER.writeValueAsString(bean);
//    }
//
//    public static QueryInfoBean create(URL url) throws IOException {
//        return MAPPER.readValue(url, QueryInfoBean.class);
//    }
//
//    public static QueryInfoBean create(String jsonString) throws IOException {
//        return MAPPER.readValue(jsonString, QueryInfoBean.class);
//    }
//}
