### 漏斗查询

查询示例

```json
{
  "queryType": "FUNNEL",
  "tableName": "yiguan",
  "aggregation": {
    "postGroup": {
      "column": "good_name",
      "rangePairs": [],
      "funnelIndex": 1
    },
    "timeWindow": 86400,
    "columns": {
      "event": "event_type",
      "userId": "userId",
      "combine": "combine"
    },
    "funnelEvents": [
      "login",
      "browseGoods",
      "consultGoods"
    ],
    "dayFilter": {
      "dayStart": "20180601",
      "numberOfDays": 10
    },
    "associatedFilter": {
      "column": "good_name",
      "funnelIndexes": [
        1,
        2
      ]
    }
  },
  "postAggregations": [
    {
      "type": "FUNNEL_MEDIAN"
    }
  ]
}
```

属性解释

| 属性名称           | 解释                                                         | required? |
| ------------------ | ------------------------------------------------------------ | --------- |
| queryType          | 查询类型                                                     | yes       |
| tableName          | 表名                                                         | yes       |
| aggregation        | 漏斗聚合函数属性                                             | yes       |
| postAggregations | 根据aggregation函数的聚合结果再计算的函数FUNNEL_MEDIAN类型表示相邻漏斗事件之间的时间差的中位数 | no        |

aggregation属性

| 属性名称           | 解释                                                         | required? |
| ------------------ | ------------------------------------------------------------ | --------- |
| `timeWindow`       | 漏斗时间窗口, 单位秒                                         | yes       |
| `funnelEvents`     | 漏斗事件                                                     | yes       |
| `columns`          | 漏斗计算用到参数字段列字段名称解释<br>`event`标识漏斗事件的字段列<br>`userId`ID列, 表示观察漏斗事件转化率的个体<br>`combine`导入数据生成的组合字段, 是一个64位long值.该long值高32位是时间戳, 单位秒低32位是漏斗事件全局字典序号, 和日期(年月日)的全局字典序号 | yes       |
| `dayFilter`        | 日期区间过滤, 精确到天                                       | yes       |
| `associatedFilter` | 属性关联过滤. 比如<br>`{   "column": "good_name",   "funnelIndexes": [1, 2]}`}  <br>表示第二个(browseGoods)和第三个(consultGoods)漏斗事件的good_name要相同 | no        |
| `postGroup`        | 结果分组属性解释<br>column作为分组的字段<br>`funnelIndex`取哪个漏斗事件对应的分组值<br>注意: funnelIndex和rangePairs属性二选一`rangePairs`(可选)如果字段是数值类型可以用数值区间分组, 比如比如[[1000, 2000], [2000, 3000], [3000, MAX]]表示按照1000-2000, 2000-3000, >=3000分组 | no        |



备注:

1、当前版本的实现要求对导入数据做一些预处理

2、只实现了部分功能