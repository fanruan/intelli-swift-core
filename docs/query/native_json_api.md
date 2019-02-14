## JSON API

### 1、明细查询

示例:

```json
{
  "queryType": "DETAIL",
  "fetchSize": 200,
  "tableName": "table_name",
  "filter": {
    "type": "STRING_STARTS_WITH",
    "filterValue": "foo",
    "column": "field_name_a"
  },
  "dimensions": [
    {
      "column": "field_name_a",
      "alias": "alias_name_b",
      "type": "DETAIL"
    },
    {
      "column": "field_name_b",
      "alias": "alias_name_b",
      "type": "DETAIL"
    }
  ]
}
```

属性解释

| property     | description  | required? |
| ------------ | ------------ | --------- |
| `queryType`  | 查询类型     | yes       |
| `fetchSize`  | 分页计算函数 | no        |
| `filter`     | 过滤条件     | no        |
| `dimensions` | 选择的字段   | yes       |



### 2、明细查询排序

示例

```json
{
  "queryType": "DETAIL_SORT",
  "fetchSize": 200,
  "tableName": "table_name",
  "filter": {
    "type": "STRING_STARTS_WITH",
    "filterValue": "foo",
    "column": "field_name_a"
  },
  "dimensions": [
    {
      "column": "field_name_a",
      "alias": "alias_name_b",
      "type": "DETAIL"
    },
    {
      "column": "field_name_b",
      "alias": "alias_name_b",
      "type": "DETAIL"
    }
  ],
  "sorts": [
    {
      "type": "DESC",
      "name": "alias_name_b"
    }
  ]
}
```

属性解释

| property     | description  | required? |
| ------------ | ------------ | --------- |
| `queryType`  | 查询类型     | yes       |
| `fetchSize`  | 分页计算函数 | no        |
| `filter`     | 过滤条件     | no        |
| `dimensions` | 选择的字段   | yes       |
| `sorts`      | 排序属性     | yes       |



### 3、分组查询

示例

```json
{
  "queryType": "GROUP",
  "fetchSize": 200,
  "filter": {
    "type": "IN",
    "filterValue": [
      "bar",
      "foo"
    ],
    "column": "field_a"
  },
  "dimensions": [
    {
      "column": "field_name",
      "alias": "alias_name",
      "type": "DETAIL"
    }
  ],
  "aggregations": [
    {
      "type": "SUM",
      "column": "sales",
      "alias": "sales_sum",
      "filter": null
    },
    {
      "type": "COUNT",
      "column": "num",
      "alias": "count",
      "filter": null
    }
  ],
  "postAggregations": [
    {
      "type": "CAL_FIELD",
      "calField": {
        "type": "ARITHMETIC_DIV",
        "name": "average",
        "parameters": [
          "sales_sum",
          "count"
        ]
      }
    },
    {
      "type": "ROW_SORT",
      "sortBeans": [
        {
          "type": "DESC",
          "name": "average"
        }
      ]
    }
  ]
}
```

属性解释

| property           | description                                                  | required? |
| ------------------ | ------------------------------------------------------------ | --------- |
| `queryType`        | 查询类型                                                     | yes       |
| `fetchSize`        | 分页计算函数                                                 | no        |
| `filter`           | 过滤条件                                                     | no        |
| `dimensions`       | 维度字段                                                     | no        |
| `aggregations`     | 聚合计算                                                     | no        |
| `postAggregations` | 结果集再计算eg:<br>select tmp.sum_a / tmp.count_b as avg_value<br>from<br>      (select sum(a) as sum_a, count(b) as sum_b <br>                from table_xxx) tmp | no        |



### 属性详解

| property    | `queryType`                                                  |
| ----------- | ------------------------------------------------------------ |
| description | 查询类型扩展开发提示: 对应[查询流程中第三、四阶段]()合并的Merger实现 |
| type        | string                                                       |
| enums       | [DETAIL, DETAIL_SORT, GROUP, ...]                            |



| property    | `filter`                                                     |
| ----------- | ------------------------------------------------------------ |
| description | 明细过滤条件. 这边过滤的明细的行号, 即过滤出底层参与计算的数据的行号. 注意区分结果过滤(在postAggregation的函数中实现)扩展开发提示: 根据过滤类型准备对应的过滤器bean对象, 根据自定义的filterValue实现明细过滤器(参考底层数据读取接口, 通常是索引过滤) |
| type        | string                                                       |
| type enums  | [AND, OR, NOT, IN, STRING_LIKE, STRING_STARTS_WITH, STRING_ENDS_WITH, NUMBER_IN_RANGE, ...] |

and过滤示例

```json
{
  "filterValue": [
    {
      "type": "STRING_STARTS_WITH",
      "filterValue": "foo",
      "column": "field_name_a"
    },
    {
      "type": "IN",
      "filterValue": [
        "bar",
        "foo"
      ],
	  "column": "field_name_a"
    }
  ],
  "type": "AND"
}
```

逻辑过滤器组合示例

```json
{
  "filterValue": [
    {
      "filterValue": [
        {
          "type": "STRING_STARTS_WITH",
          "filterValue": "foo",
          "column": "field_name_a"
        },
        {
          "type": "IN",
          "filterValue": [
            "bar",
            "foo"
          ],
          "column": "field_name_b"
        }
      ],
      "type": "AND"
    },
    {
      "filterValue": [
        {
          "filterValue": {
            "type": "STRING_STARTS_WITH",
            "filterValue": "foo",
	        "column": "field_name_a"
          },
          "type": "NOT"
        },
        {
          "type": "IN",
          "filterValue": [
            "bar2",
            "foo1"
          ],
          "column": "field_name_a"
        }
      ],
      "type": "OR"
    }
  ],
  "type": "AND"
}
```



| property    | `dimensions`                                                 |
| ----------- | ------------------------------------------------------------ |
| description | 维度字段扩展开发提示: 可以通过扩展bean属性, 支持维度值转换函数, 重新分组等 |
| type        | string                                                       |
| type enums  | [DETAIL, GROUP, ...]                                         |
| 示例        |                                                              |



| property    | `aggregations`                                               |
| ----------- | ------------------------------------------------------------ |
| description | 聚合计算扩展开发提示:增加type对应的Aggretator实现输入: 底层列数据读取接口, 索引过滤行号输出: 聚合值(满足分布式计算的数据结构) |
| type        | string                                                       |
| type enums  | [SUM, MAX, MIN, AVERAGE,COUNT,DISTINCT,HLL_DISTINCT,, ...]   |
| 示例        |                                                              |



| property    | `postAggregations`                                           |
| ----------- | ------------------------------------------------------------ |
| description | 结果集再计算(可以再聚合)扩展开发提示:增加type对应实现输入: QueryResultSet输出: QueryResultSet参考QueryResultSet接口 |
| type        | string                                                       |
| type enums  | [CAL_FIELD,HAVING_FILTER,ROW_SORT, ...]                      |
| 示例        |                                                              |

