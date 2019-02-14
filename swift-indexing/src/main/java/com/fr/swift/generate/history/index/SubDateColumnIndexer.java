package com.fr.swift.generate.history.index;

import com.fr.swift.generate.ColumnIndexer;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.impl.SubDateColumn;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/23
 */
public class SubDateColumnIndexer<Derive> extends ColumnIndexer<Derive> {
    protected GroupType type;

    public SubDateColumnIndexer(ColumnKey key, GroupType type, List<Segment> segments) {
        super(key, segments);
        this.type = type;
    }

    @Override
    protected Column<Derive> getColumn(Segment segment) {
        return (Column<Derive>) new SubDateColumn(super.getColumn(segment), type);
    }
}