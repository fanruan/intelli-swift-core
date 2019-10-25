package com.fr.swift.query.group;

import com.fr.swift.source.core.CoreService;

/**
 * @author anchore
 * @date 2018/1/29
 */
public interface GroupRule extends CoreService {
    /**
     * 分组类型
     *
     * @return 类型
     */
    GroupType getGroupType();
}