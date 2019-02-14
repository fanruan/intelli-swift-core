package com.fr.swift.query.info.bean.type;

/**
 * Created by Lyon on 2018/6/3.
 */
public enum PostQueryType {

    /**
     * 配置类计算，生成一列
     */
    CAL_FIELD,

    /**
     * 结果二维表的行过滤。这个可以包含到树过滤里面，但由于这个能对应到类似sql的having过滤条件，所以加个分类
     */
    HAVING_FILTER,

    /**
     * 结果二维表的树过滤，比如对依据父节点的聚合结果进行过滤
     */
    TREE_FILTER,

    /**
     * 结果二维表的树状聚合，对应bi功能的节点合计
     */
    TREE_AGGREGATION,

    /**
     * 结果二维表的树状排序。比如order by field1(维度字段), field2(数值字段)，这个就是树状排序
     */
    TREE_SORT,

    /**
     * 结果二维表的行排序。比如order by field1(数值字段), field2(数值字段)，做完这个操作之后，树结构被破坏了
     */
    ROW_SORT,
    /**
     * 漏斗中位数计算。
     */
    FUNNEL_MEDIAN
}
