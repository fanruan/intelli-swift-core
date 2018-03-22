package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.Column;

/**
 * @author anchore
 * @date 2018/1/26
 */
class PlainGroupOperator<Base, Derive> implements GroupOperator<Base, Derive> {
    private GroupRule<Base, Derive> rule;

    PlainGroupOperator(GroupRule<Base, Derive> rule) {
        this.rule = rule;
    }

    @Override
    public Column<Derive> group(Column<Base> column) {
        return new GroupColumn<Base, Derive>(column, rule);
    }
}