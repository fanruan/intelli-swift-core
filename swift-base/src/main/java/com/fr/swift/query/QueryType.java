package com.fr.swift.query;

/**
 * @author pony
 * @date 2017/12/12
 */
public enum QueryType {

    /**
     * 明细查询
     */
    DETAIL,

    /**
     * 分组聚合
     */
    GROUP,

    // todo 这个交叉表的考虑去掉
    CROSS_GROUP,

    /**
     * 多个聚合结果集根据维度字段进行拼接
     */
    RESULT_JOIN,

    /**
     * 远程全部查询，目标远程节点包含当前用户查询的所有数据分块。该类型仅内部使用！
     */
    REMOTE_ALL,

    /**
     * 远程部分查询，目标远程节点包含当前用户查询的部分数据分块。该分类仅内部使用！
     */
    REMOTE_PART
}
