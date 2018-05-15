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
     * 这边只用来取结果过滤器。维度里面的明细过滤要在适配层各个组件分别解析
     *
     * @return
     */
    FilterInfo getFilter();
}