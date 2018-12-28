package com.fr.swift.boot.controller.test;


import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.MetaDataColumnBean;
import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.source.resultset.importing.SwiftImportResultSet;
import com.fr.swift.source.resultset.importing.file.SingleStreamImportResultSet;
import com.fr.swift.source.resultset.importing.file.impl.TabLineParser;
import com.fr.swift.source.resultset.progress.ProgressResultSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@RestController
public class TestController {

    //    @ResponseBody
//    @RequestMapping(value = "swift/test/string", method = RequestMethod.GET)
//    public String testPrint(HttpServletResponse response, HttpServletRequest request) {
//        System.out.println("swift/test");
//        return String.valueOf(System.currentTimeMillis());
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "swift/test/map", method = RequestMethod.GET)
//    public Object testPrint1(HttpServletResponse response, HttpServletRequest request) {
//        System.out.println("swift/test");
//        Map map = new HashMap();
//        map.put("1", System.currentTimeMillis());
//        return map;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "swift/test/{id}", method = RequestMethod.GET)
//    public Object testPrint2(HttpServletResponse response, HttpServletRequest request, @PathVariable("id") String id) {
//        System.out.println("swift/test");
//        List<String> list = new ArrayList<String>();
//        list.add(String.valueOf(System.currentTimeMillis()));
//        list.add(String.valueOf(System.currentTimeMillis()));
//        list.add(String.valueOf(System.currentTimeMillis()));
//        return list;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "swift/test/postdata", method = RequestMethod.POST)
//    public Object testPrint2(HttpServletResponse response, HttpServletRequest request,
//                             @RequestBody(required = false) Map<String, Object> requestMap) {
//        System.out.println("swift/test");
//        List<String> list = new ArrayList<String>();
//        list.add(String.valueOf(System.currentTimeMillis()));
//        list.add(String.valueOf(System.currentTimeMillis()));
//        list.add(String.valueOf(System.currentTimeMillis()));
//        return list;
//    }
    @RequestMapping("swift/import")
    @ResponseBody
    public Boolean testImport(String path) throws Exception {
        Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey("test_yiguan"));
        SwiftImportResultSet resultSet = new SingleStreamImportResultSet(table.getMetadata(), path, new TabLineParser(false));
        HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey("test_yiguan"), new LineAllotRule(10000));

        HistoryBlockImporter importer = new HistoryBlockImporter(table,
                alloter);
        importer.importData(new ProgressResultSet(new LimitedResultSet(resultSet, 1000000), "test_yiguan"));
        return true;
    }

    @RequestMapping("swift/createTable")
    @ResponseBody
    public Boolean createTable() {
        SwiftMetaData metaData = new SwiftMetaDataBean();
        ((SwiftMetaDataBean) metaData).setId("test_yiguan");
        ((SwiftMetaDataBean) metaData).setSwiftDatabase(SwiftDatabase.CUBE);
        ((SwiftMetaDataBean) metaData).setTableName("test_yiguan");
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        metaDataColumns.add(new MetaDataColumnBean("id", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("currentTime", Types.TIMESTAMP));
        metaDataColumns.add(new MetaDataColumnBean("eventType", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("eventName", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("json", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("longValue", Types.BIGINT));

        ((SwiftMetaDataBean) metaData).setFields(metaDataColumns);
        SwiftContext.get().getBean(SwiftMetaDataService.class).addMetaData("test_yiguan", metaData);
        return true;
    }

    @RequestMapping("swift/query")
    @ResponseBody
    public QueryBean test() throws Exception {
        String json = "{\"filter\":{\"filterValue\":[{\"filterValue\":[{\"filterValue\":[{\"filterValue\":{\"endIncluded\":false,\"start\":\"1545667200000\",\"startIncluded\":true},\"column\":\"time\",\"type\":\"NUMBER_IN_RANGE\"},{\"filterValue\":{\"endIncluded\":true,\"end\":\"1545839999000\",\"startIncluded\":false},\"column\":\"time\",\"type\":\"NUMBER_IN_RANGE\"}],\"type\":\"AND\"}],\"type\":\"AND\"}],\"type\":\"AND\"},\"fetchSize\":200,\"sorts\":[{\"name\":\"time\",\"type\":\"DESC\"}],\"tableName\":\"fine_record_operate\",\"dimensions\":[{\"column\":\"time\",\"type\":\"DETAIL\"},{\"column\":\"type\",\"type\":\"DETAIL\"},{\"column\":\"item\",\"type\":\"DETAIL\"},{\"column\":\"resource\",\"type\":\"DETAIL\"},{\"column\":\"operate\",\"type\":\"DETAIL\"},{\"column\":\"username\",\"type\":\"DETAIL\"},{\"column\":\"detail\",\"type\":\"DETAIL\"},{\"column\":\"ip\",\"type\":\"DETAIL\"},{\"column\":\"requestParam\",\"type\":\"DETAIL\"}],\"queryType\":\"DETAIL\",\"queryId\":\"e630c0c3-6266-4a4a-a739-e04e741396ed\"}";
        QueryBean queryBean = QueryBeanFactory.create(json);
        return queryBean;
    }
}
