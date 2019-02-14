package com.fr.swift.query.group;

import com.fr.swift.source.core.CoreService;

/**
 * @author pony
 * @date 2017/12/11
 */
public interface Group<Base, Derive> extends CoreService {
    GroupOperator<Base, Derive> getGroupOperator();

    GroupType getGroupType();
}