package com.fr.swift.api.test;

import com.fr.swift.api.SwiftApiConstants;
import com.fr.swift.api.test.bean.SimpleDetailQueryBean;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryInfoBeanFactory;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.third.springframework.stereotype.Controller;
import com.fr.third.springframework.web.bind.annotation.PathVariable;
import com.fr.third.springframework.web.bind.annotation.RequestBody;
import com.fr.third.springframework.web.bind.annotation.RequestMapping;
import com.fr.third.springframework.web.bind.annotation.RequestMethod;
import com.fr.third.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/6/21
 */
@Controller
@RequestMapping(SwiftApiConstants.TEST_ROOT_URL)
public class TestQueryController {

    @ResponseBody
    @RequestMapping(value = "/query/{sourceKey}", method = RequestMethod.GET)
    public List<Row> query(@PathVariable("sourceKey") String jsonString) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        long start = System.currentTimeMillis();
        QueryBean queryBean = QueryInfoBeanFactory.create(jsonString);
        ((DetailQueryInfoBean) queryBean).setQueryId("" + System.currentTimeMillis());
        Query query = QueryBuilder.buildQuery(queryBean);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        SwiftLoggers.getLogger().info("group query cost: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds!");
        return rows;
    }

    /**
     * postman测试 post请求，requestBody填：
     * {
     * "table": "sourceKey",
     * "columns": ["字段1", "字段2"]
     * }
     *
     * @param bean
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/simpleQuery", method = RequestMethod.POST)
    @ResponseBody
    public List<Row> query(@RequestBody SimpleDetailQueryBean bean) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        long start = System.currentTimeMillis();
        QueryBean queryBean = bean.toQueryBean();
        Query query = QueryBuilder.buildQuery(queryBean);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        SwiftLoggers.getLogger().info("group query cost: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds!");
        return rows;
    }

    @ResponseBody
    @RequestMapping(value = "/group/{sourceKey}", method = RequestMethod.GET)
    public List<Row> groupQuery(@PathVariable("sourceKey") String jsonString) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        // swift-test模块的resources目录下有json示例
        QueryBean queryBean = QueryInfoBeanFactory.create(jsonString);
        ((GroupQueryInfoBean) queryBean).setQueryId("" + System.currentTimeMillis());
        long start = System.currentTimeMillis();
        Query query = QueryBuilder.buildQuery(queryBean);
        SwiftResultSet resultSet = query.getQueryResult();
        if (resultSet != null) {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        SwiftLoggers.getLogger().info("group query cost: " + TimeUnit.MILLISECONDS.toMillis(System.currentTimeMillis() - start) + " ms!");
        return rows;
    }
}
