package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.CoreGenerator;

/**
 * @author anchore
 * @date 2018/1/26
 */
abstract class BaseGroup<Base, Derive> implements Group<Base, Derive> {
    @CoreField
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
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch(Exception ignore) {

        }
        return Core.EMPTY_CORE;
    }
}