package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.Column;

/**
 * @author anchore
 * @date 2018/1/26
 */
abstract class BaseGroup implements Group {
    private GroupType type;
    private GroupRule rule;

    BaseGroup(GroupType type, GroupRule rule) {
        this.type = type;
        this.rule = rule;
    }

    @Override
    public GroupType getGroupType() {
        return type;
    }

    @Override
    public GroupOperator getGroupOperator() {
        return new BaseGroupOperator() {
            @Override
            public Column<String> group(Column<?> column) {
                return new GroupColumn(column.getBitmapIndex(), rule);
            }
        };
    }
}