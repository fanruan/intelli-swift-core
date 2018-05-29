package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.adapter.SwiftColumnProvider;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.Sort;

/**
 * @author pony
 * @date 2017/12/11
 * 维度
 */
public interface Dimension extends SwiftColumnProvider {
    Sort getSort();

    Group getGroup();

    /**
     * 这个接口应该去掉，通用查询这边只处理到最后一个维度的结果过滤，这个结果过滤在postAggregation里面做
     * @return
     */
    FilterInfo getFilter();
}