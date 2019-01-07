package com.fr.swift.query.group.by;

import com.fr.swift.query.group.info.GroupByInfo;

/**
 * Created by Lyon on 2018/3/28.
 */
public class MultiGroupByIndex extends MultiGroupBy<int[]> {

    /**
     * 单个segment使用的迭代器
     */
    public MultiGroupByIndex(GroupByInfo groupByInfo) {
        super(groupByInfo);
    }

    @Override
    protected int[] createKey(int[] indexes) {
        // TODO: 2019/1/3 如果列做了全局字典，可以转成全局字典
        return indexes;
    }
}
