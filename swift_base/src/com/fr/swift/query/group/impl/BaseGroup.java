package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;

/**
 * @author anchore
 * @date 2018/1/26
 */
abstract class BaseGroup<Base, Derive> implements Group<Base, Derive> {
    private GroupRule<Base, Derive> rule;

    BaseGroup(GroupRule<Base, Derive> rule) {
        this.rule = rule;
    }

    @Override
    public GroupType getGroupType() {
        return rule.getGroupType();
    }

    @Override
    public GroupOperator<Base, Derive> getGroupOperator() {
        return new PlainGroupOperator<Base, Derive>(rule);
    }
}