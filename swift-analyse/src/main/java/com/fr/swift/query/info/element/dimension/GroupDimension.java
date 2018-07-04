package com.fr.swift.query.info.element.dimension;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.ColumnKey;

/**
 * Created by pony on 2017/12/25.
 */
public class GroupDimension extends AbstractDimension {
    public GroupDimension(int index, ColumnKey columnKey, Group group, Sort sort) {
        super(index, columnKey, group, sort);
    }

    @Override
    public DimensionType getDimensionType() {
        return DimensionType.GROUP;
    }
}
