# Demo

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