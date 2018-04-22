package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.Column;

/**
 * @author anchore
 * @date 2018/4/22
 */
public class GroupWrapper<Base, Derive> extends BaseGroup<Base, Derive> {
    private Group<Base, Derive> originGroup;

    public GroupWrapper(Group<Base, Derive> originGroup, GroupRule rule) {
        super(rule);
        this.originGroup = originGroup;
    }

    @Override
    public GroupOperator<Base, Derive> getGroupOperator() {
        return new GroupOperator<Base, Derive>() {
            @Override
            public Column<Derive> group(Column<Base> column) {
                GroupOperator<Derive, Derive> curOp = (GroupOperator<Derive, Derive>) GroupWrapper.super.getGroupOperator();
                return curOp.group(originGroup.getGroupOperator().group(column));
            }
        };
    }
}