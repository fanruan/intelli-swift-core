package com.fr.swift.boot.controller.test;


import com.fr.swift.SwiftContext;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.compare.Comparators;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.db.Table;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.info.bean.element.DimensionBean;
import com.fr.swift.query.info.bean.element.MetricBean;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.insert.HistoryBlockImporter;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.alloter.impl.hash.HashAllotRule;
import com.fr.swift.source.alloter.impl.hash.HistoryHashSourceAlloter;
import com.fr.swift.source.alloter.impl.line.HistoryLineSourceAlloter;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.source.resultset.importing.SwiftImportResultSet;
import com.fr.swift.source.resultset.importing.file.FileLineParser;
import com.fr.swift.source.resultset.importing.file.SingleStreamImportResultSet;
import com.fr.swift.source.resultset.importing.file.impl.TabLineParser;
import com.fr.swift.source.resultset.progress.ProgressResultSet;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    @RequestMapping("swift/importYiguan")
    @ResponseBody
    public Boolean testYiguanImport(String path) throws Exception {
        final String dayStart = "20180601";
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final long millisOn201861 = format.parse(dayStart).getTime();
        Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey("test_yiguan"));
        SwiftImportResultSet resultSet = new SingleStreamImportResultSet(table.getMetadata(),
                path, new TabLineParser(false, new FileLineParser.LineParserAdaptor() {
            @Override
            public Row adapt(Row row) {
                // [10003409308807394455, 1528410985, browseGoods, 浏览商品, {"name":"watch","city":"长沙","brand":"Apple","price":2608.283}, 20180608]
                List data = new ArrayList();
                data.add(row.getValue(0));  // id
                data.add(row.getValue(1));  // timestamp
                data.add(row.getValue(2));  // eventType
                try {
                    Map map = JsonBuilder.readValue((String) row.getValue(4), Map.class);
                    data.add(map.get("name"));
                    data.add(map.get("city"));
                    data.add(map.get("brand"));
                    Double price = (Double) map.get("price");
                    data.add(price == null ? null : price.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String day = row.getValue(row.getSize() - 1);
                data.add(day);  // date
                long timestamp = Long.parseLong((String) data.get(1));
                long eventType = GLOBAL_TYPE_DICT.get(data.get(2));
                try {
                    long dayIndex = TimeUnit.MILLISECONDS.toDays(format.parse(day).getTime() - millisOn201861);
                    Long combine = (timestamp << 32) | (eventType << 16) | dayIndex;
                    data.add(combine.toString()); // combine
                } catch (Exception ig) {
                }
                return new ListBasedRow(data);
            }
        }));
        HistoryHashSourceAlloter alloter = new HistoryHashSourceAlloter(
                new SourceKey("test_yiguan"),
                new HashAllotRule(0, 10));
        HistoryBlockImporter importer = new HistoryBlockImporter(table, alloter);
        importer.importData(new ProgressResultSet(new LimitedResultSet(resultSet, 2000000), "test_yiguan"));
        SwiftSegmentService service = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        List<SegmentKey> segmentKeys = service.getSegmentByKey("test_yiguan");
        List<Segment> segments = new ArrayList<Segment>();
        for (SegmentKey key : segmentKeys) {
            segments.add(SegmentUtils.newSegment(key));
        }
        SegmentUtils.indexSegmentIfNeed(segments);
        return true;
    }

    @RequestMapping("swift/createYiguanTable")
    @ResponseBody
    public Boolean createYiguanTable() {
        SwiftMetaData metaData = new SwiftMetaDataBean();
        ((SwiftMetaDataBean) metaData).setId("test_yiguan");
        ((SwiftMetaDataBean) metaData).setSwiftDatabase(SwiftDatabase.CUBE);
        ((SwiftMetaDataBean) metaData).setTableName("test_yiguan");
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        metaDataColumns.add(new MetaDataColumnBean("id", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("currentTime", Types.TIMESTAMP));
        metaDataColumns.add(new MetaDataColumnBean("eventType", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("name", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("city", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("brand", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("price", Types.DOUBLE));
        metaDataColumns.add(new MetaDataColumnBean("date", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("combine", Types.BIGINT));

        ((SwiftMetaDataBean) metaData).setFields(metaDataColumns);
        SwiftContext.get().getBean(SwiftMetaDataService.class).addMetaData("test_yiguan", metaData);
        return true;
    }

    @RequestMapping("swift/query/group")
    @ResponseBody
    public Object testGroup() throws Exception {
        GroupQueryInfoBean query = new GroupQueryInfoBean();
        query.setQueryId(UUID.randomUUID().toString());
        query.setTableName("test_yiguan");
        DimensionBean dimensionBean = new DimensionBean();
        dimensionBean.setType(DimensionType.GROUP);
        dimensionBean.setColumn("eventType");
        MetricBean metricBean = new MetricBean();
        metricBean.setType(AggregatorType.COUNT);
        metricBean.setColumn("id");
        query.setDimensions(Arrays.asList(dimensionBean));
        query.setAggregations(Arrays.asList(metricBean));
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(query)), query);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }

    @RequestMapping("swift/query/funnel")
    @ResponseBody
    public Object testFunnel() throws Exception {
        QueryBean bean = JsonBuilder.readValue(funnelQuery, FunnelQueryBean.class);
        bean.setQueryId(UUID.randomUUID().toString());
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(bean)), bean);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }

    @RequestMapping("swift/query/detail")
    @ResponseBody
    public Object testDetail() throws Exception {
        DetailQueryInfoBean query = new DetailQueryInfoBean();
        query.setTableName("test_yiguan");
        query.setQueryId(UUID.randomUUID().toString());
        DimensionBean dimensionBean = new DimensionBean();
        dimensionBean.setType(DimensionType.DETAIL_ALL_COLUMN);
        query.setDimensions(Arrays.asList(dimensionBean));
        AnalyseService service = ProxySelector.getInstance().getFactory().getProxy(AnalyseService.class);
        SwiftResultSet resultSet = SwiftResultSetUtils.toSwiftResultSet(
                service.getQueryResult(QueryBeanFactory.queryBean2String(query)), query);
        List<Row> rows = new ArrayList<Row>();
        int limit = 200;
        while (resultSet.hasNext() && limit-- > 0) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }

    private static String funnelQuery = "{\n" +
            "  \"fetchSize\": 200,\n" +
            "  \"aggregation\": {\n" +
            "    \"timeWindow\": 2592000,\n" +
            "    \"columns\": {\n" +
            "      \"date\": \"date\",\n" +
            "      \"event\": \"eventType\",\n" +
            "      \"userId\": \"id\",\n" +
            "      \"combine\": \"combine\"\n" +
            "    },\n" +
            "    \"funnelEvents\": [\n" +
            "      \"login\",\n" +
            "      \"browseGoods\",\n" +
            "      \"addCart\"\n" +
            "    ],\n" +
            "    \"dayFilter\": {\n" +
            "      \"column\": \"date\",\n" +
            "      \"dayStart\": \"20180601\",\n" +
            "      \"numberOfDays\": 30\n" +
            "    }\n" +
            "  },\n" +
            "  \"postAggregations\": [],\n" +
            "  \"sorts\": [],\n" +
            "  \"tableName\": \"test_yiguan\",\n" +
            "  \"dimensions\": [],\n" +
            "  \"queryType\": \"FUNNEL\"\n" +
            "}";

    private static final List<String> events = Arrays.asList("addCart", "browseGoods", "collectionGoods", "confirm", "consultGoods", "evaluationGoods", "login", "order", "orderPayment", "reminding", "searchGoods", "shareGoods", "startUp", "unsubscribeGoods", "viewOrder");

    private static final Map<String, Long> GLOBAL_TYPE_DICT = new TreeMap<String, Long>(Comparators.STRING_ASC);

    static {
        for (String key : events) {
            GLOBAL_TYPE_DICT.put(key, (long) (GLOBAL_TYPE_DICT.size() + 1));
        }
    }
}
