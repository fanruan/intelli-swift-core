package com.fr.swift.query.info.dimension;

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
}