package com.fr.swift.boot.controller.test;

import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.boot.controller.SwiftApiConstants;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.Row;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/6/21
 */
@RestController
@RequestMapping(SwiftApiConstants.TEST_ROOT_URL)
public class TestQueryController {


    @ResponseBody
    @RequestMapping(value = "/query/{sourceKey}", method = RequestMethod.GET)
    public List<Row> query(@PathVariable("sourceKey") String jsonString) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        int count = 200;
        long start = System.currentTimeMillis();
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        // TODO: 2018/11/28
        SwiftResultSet resultSet = (SwiftResultSet) service.getQueryResult(jsonString);
        if (resultSet != null) {
            while (resultSet.hasNext() && count-- > 0) {
                rows.add(resultSet.getNextRow());
            }
            resultSet.close();
        }
        SwiftLoggers.getLogger().info("group query cost: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds!");
        return rows;
    }

//    @RequestMapping(value = "/simpleQuery", method = RequestMethod.POST)
//    @ResponseBody
//    public List<Row> query(@RequestBody SimpleDetailQueryBean bean) throws Exception {
//        List<Row> rows = new ArrayList<Row>();
//        long start = System.currentTimeMillis();
//        QueryBean queryBean = bean.toQueryBean();
//        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
//        // TODO: 2018/11/28
//        SwiftResultSet resultSet = (SwiftResultSet) service.getQueryResult(QueryBeanFactory.queryBean2String(queryBean));
//        if (resultSet != null) {
//            while (resultSet.hasNext()) {
//                rows.add(resultSet.getNextRow());
//            }
//            resultSet.close();
//        }
//        SwiftLoggers.getLogger().info("group query cost: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) + " seconds!");
//        return rows;
//    }

    @ResponseBody
    @RequestMapping(value = "/group/{sourceKey}", method = RequestMethod.GET)
    public List<Row> groupQuery(@PathVariable("sourceKey") String jsonString) throws Exception {
        List<Row> rows = new ArrayList<Row>();
        // swift-test模块的resources目录下有json示例
        long start = System.currentTimeMillis();
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = (SwiftResultSet) service.getQueryResult(jsonString);
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
