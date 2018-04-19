package com.fr.swift.generate.history.index;

import com.fr.swift.generate.BaseColumnDictMerger;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.DateColumn;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * This class created on 2018/4/18
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */

public class SubDateColumnDictMerger<Derive> extends BaseColumnDictMerger<Derive> {

    protected GroupType type;

    public SubDateColumnDictMerger(DataSource dataSource, ColumnKey key, GroupType type, List<Segment> segments) {
        super(dataSource, key, segments);
        this.type = type;
    }

    @Override
    protected Column<Derive> getColumn(Segment segment) {
        return (Column<Derive>) new SubDateColumn(((DateColumn) super.getColumn(segment)), type);
    }

}