package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.CustomGroupRule;
import com.fr.swift.query.group.GroupOperator;
import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.util.Crasher;

import java.util.List;

/**
 * @author anchore
 * @date 2018/1/26
 */
class PlainGroupOperator<Base, Derive> implements GroupOperator<Base, Derive> {
    private GroupRule rule;

    PlainGroupOperator(GroupRule rule) {
        this.rule = rule;
    }

    @Override
    public Column<Derive> group(Column<Base> column) {
        switch (rule.getGroupType()) {
            case AUTO:
            case CUSTOM_NUMBER:
            case CUSTOM:
            case CUSTOM_SORT:
                return new CustomGroupColumn<Base, Derive>(column, (CustomGroupRule<Base, Derive>) rule);
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
                return (Column<Derive>) new SubDateColumn(column, rule.getGroupType());
            case NONE:
                return (Column<Derive>) column;
            default:
                return Crasher.crash("no type fits");
        }
    }

    @Override
    public Column<Derive> group(List<Column<Base>> columnList) {
        switch (rule.getGroupType()) {
            case OTHER_DIMENSION_SORT:
                return new SortByOtherDimensionColumn<Base, Derive>(columnList, (BaseSortByOtherDimensionGroupRule<Base, Derive>) rule);
            default:
                return group(columnList.get(0));
        }
    }
}