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
    private GroupRule rule;

    BaseGroup(GroupRule rule) {
        this.rule = rule;
    }

    @Override
    public GroupOperator<Base, Derive> getGroupOperator() {
        return new PlainGroupOperator<Base, Derive>(rule);
    }

    @Override
    public GroupType getGroupType() {
        return rule.getGroupType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseGroup<?, ?> baseGroup = (BaseGroup<?, ?>) o;

        return rule != null ? rule.getGroupType().equals(baseGroup.rule.getGroupType()) : baseGroup.rule == null;
    }

    @Override
    public int hashCode() {
        return rule.getGroupType() != null ? rule.getGroupType().hashCode() : 0;
    }
}