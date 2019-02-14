package com.fr.swift.query.info.element.dimension;

import com.fr.swift.query.group.Group;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractDimension extends AbstractQueryColumn implements Dimension {

    private ColumnKey columnKey;
    private Group group;
    private Sort sort;
    private IndexInfo indexInfo;

    public AbstractDimension(int index, ColumnKey columnKey, Group group, Sort sort, IndexInfo indexInfo) {
        super(index);
        this.columnKey = columnKey;
        this.group = group;
        this.sort = sort;
        this.indexInfo = indexInfo;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    @Override
    public Column getColumn(Segment segment) {
        return segment.getColumn(columnKey);
    }

    @Override
    public IndexInfo getIndexInfo() {
        return indexInfo;
    }
}
