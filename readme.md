
## Swift

Swift是基于分布式、列式存储和实时计算的高性能数据仓库系统，适用于对海量数据进行高效实时分析的场景。

Swift作为数据仓库提供一系列高性能聚合实现，核心库中包括常用的过滤、分组、聚合等计算的实现。同时，基于轻量、最少依赖、易扩展的设计理念，使得开发者可以很容易基于Swift完成系统集成和二次开发需求。

### Documentation
[详见](docs/query/native_json_api.md)

### Getting Started

#### Prerequisites
requires JDK 6 or better

#### 下载
todo
#### 启动
todo
#### 使用示例
```java
@RestController
public class Demo {

    @RequestMapping("swift/demo/createTable")
    @ResponseBody
    public boolean createTable() {
        SwiftMetaDataBean metaData = new SwiftMetaDataBean();
        metaData.setId("commodity_stock");
        metaData.setSwiftDatabase(SwiftDatabase.CUBE);
        metaData.setTableName("commodity_stock");
        List<SwiftMetaDataColumn> metaDataColumns = new ArrayList<SwiftMetaDataColumn>();
        metaDataColumns.add(new MetaDataColumnBean("日期", Types.DATE));
        metaDataColumns.add(new MetaDataColumnBean("仓库", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("产品ID", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("产品名称", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("供应商ID", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("类别ID", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("单位数量", Types.VARCHAR));
        metaDataColumns.add(new MetaDataColumnBean("单价", Types.BIGINT));
        metaDataColumns.add(new MetaDataColumnBean("上月库存", Types.BIGINT));
        metaDataColumns.add(new MetaDataColumnBean("入库", Types.BIGINT));
        metaDataColumns.add(new MetaDataColumnBean("出库", Types.BIGINT));
        metaData.setFields(metaDataColumns);
        SwiftContext.get().getBean(SwiftMetaDataService.class).addMetaData("commodity_stock", metaData);
        return true;
    }

    @RequestMapping("swift/demo/load")
    @ResponseBody
    public boolean load() throws Exception {
        String path = "/Users/lyon/Downloads/commodity-stock.csv";
        Table table = com.fr.swift.db.impl.SwiftDatabase.getInstance().getTable(new SourceKey("commodity_stock"));
        SwiftImportResultSet resultSet = new SingleStreamImportResultSet(table.getMetadata(), path, new CommaLineParser(true));
        HistoryLineSourceAlloter alloter = new HistoryLineSourceAlloter(new SourceKey("commodity_stock"), new LineAllotRule(10000));
        HistoryBlockImporter importer = new HistoryBlockImporter(table, alloter);
        importer.importData(new ProgressResultSet(resultSet, "commodity_stock"));
        SwiftSegmentService service = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);
        List<SegmentKey> segmentKeys = service.getSegmentByKey("commodity_stock");
        List<Segment> segments = new ArrayList<Segment>();
        for (SegmentKey key : segmentKeys) {
            segments.add(SegmentUtils.newSegment(key));
        }
        SegmentUtils.indexSegmentIfNeed(segments);
        return true;
    }

    @RequestMapping("swift/demo/query/group")
    @ResponseBody
    public Object group() throws Exception {
        QueryBean queryBean = GroupQueryInfoBean.builder("commodity_stock")
                .setDimensions(
                        new DimensionBean(DimensionType.GROUP, "仓库"),
                        new DimensionBean(DimensionType.GROUP, "产品名称")
                )
                .setAggregations(
                        new MetricBean("单价", AggregatorType.AVERAGE),
                        new MetricBean("上月库存", AggregatorType.MAX)
                )
                .build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(queryBean);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }

    @RequestMapping("swift/demo/query/detail")
    @ResponseBody
    public Object detail() throws Exception {
        QueryBean queryBean = DetailQueryInfoBean.builder("commodity_stock")
                .setDimensions(
                        new DimensionBean(DimensionType.DETAIL_ALL_COLUMN)
                )
                .build();
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(queryBean);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }

    /**
     * queryJson:
     * {
     *   "fetchSize": 200,
     *   "aggregations": [
     *     {
     *       "column": "单价",
     *       "type": "AVERAGE"
     *     },
     *     {
     *       "column": "上月库存",
     *       "type": "MAX"
     *     }
     *   ],
     *   "postAggregations": [],
     *   "sorts": [],
     *   "tableName": "commodity_stock",
     *   "dimensions": [
     *     {
     *       "column": "仓库",
     *       "type": "GROUP"
     *     },
     *     {
     *       "column": "产品名称",
     *       "type": "GROUP"
     *     }
     *   ],
     *   "queryType": "GROUP"
     * }
     */
    @RequestMapping(value = "swift/demo/query/group", method = RequestMethod.POST)
    @ResponseBody
    public Object group(@RequestBody String queryJson) throws Exception {
        SwiftResultSet resultSet = QueryRunnerProvider.getInstance().query(queryJson);
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNext()) {
            rows.add(resultSet.getNextRow());
        }
        return rows;
    }
}
```

### Benchmark
todo

### Reporting Issues
如果发现任何bug或者有新需求，请开[GitHub issue](https://github.com/fanruan/intelli-swift-core/issues)讨论.

### License
todo

