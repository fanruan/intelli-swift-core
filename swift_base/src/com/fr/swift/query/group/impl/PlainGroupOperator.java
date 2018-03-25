package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.SubDateColumn;

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
        switch (rule.getGroupType()) {
            case AUTO:
            case CUSTOM_NUMBER:
            case CUSTOM:
                return new GroupColumn<Base, Derive>(column, rule);
            case Y_M_D_H_M_S:
            case Y_M_D_H_M:
            case Y_M_D_H:
            case Y_M_D:
            case Y_Q:
            case Y_M:
            case Y_W:
            case Y_D:
            case M_D:
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK_OF_YEAR:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
                return new SubDateColumn<Derive>((Column<Long>) column, rule.getGroupType());
            case NONE:
            default:
                return (Column<Derive>) column;
        }
    }
}