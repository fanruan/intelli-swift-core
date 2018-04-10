package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.CoreField;

/**
 * @author anchore
 * @date 2018/1/31
 * <p>
 * 不分组规则 1个一组
 * 两种情况：普通列、日期子列
 */
public class NoGroupRule extends BaseGroupRule {
    @CoreField
    private GroupType type;

    public NoGroupRule() {
        this(GroupType.NONE);
    }

    public NoGroupRule(GroupType type) {
        this.type = type;
    }

    @Override
    public GroupType getGroupType() {
        return type;
    }
}
