package com.fr.swift.query.adapter.dimension;

import com.fr.swift.query.adapter.SwiftColumnProvider;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.Sort;

/**
 * Created by pony on 2017/12/11.
 * 维度
 */
public interface Dimension extends SwiftColumnProvider {
    Sort getSort();
    Group getGroup();
    FilterInfo getFilter();
}
