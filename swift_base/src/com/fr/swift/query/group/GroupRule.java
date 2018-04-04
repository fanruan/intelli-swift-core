package com.fr.swift.query.group;

import com.fr.swift.source.core.CoreService;

/**
 * @author anchore
 * @date 2018/1/29
 */
public interface GroupRule extends CoreService {
    GroupType getGroupType();
}