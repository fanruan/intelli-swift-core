package com.fr.swift.query.info.element.dimension;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.info.bean.type.DimensionType;
import com.fr.swift.query.sort.Sort;

/**
 * @author pony
 * @date 2017/12/11
 * 维度
 */
public interface Dimension extends SwiftColumnProvider {

    // TODO: 2018/6/20 这个排序不应该放在这边。如果是分组表没问题，但是如果是明细表就有问题了。
    // TODO: 2018/6/20 要么这个Dimension仅用于分组表，要么把排序属性独立放到queryInfo中
    Sort getSort();

    Group getGroup();

    IndexInfo getIndexInfo();

    DimensionType getDimensionType();

}