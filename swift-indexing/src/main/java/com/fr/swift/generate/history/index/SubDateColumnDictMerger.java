package com.fr.swift.generate.history.index;

import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.generate.ColumnDictMerger;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
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

public class SubDateColumnDictMerger<Derive> extends ColumnDictMerger<Derive> {
    private GroupType type;

    public SubDateColumnDictMerger(DataSource dataSource, ColumnKey key, GroupType type, List<Segment> segments) {
        super(dataSource, key, segments);
        this.type = type;
    }

    @Override
    protected Column<Derive> getColumn(Segment segment) {
        return (Column<Derive>) new SubDateColumn(super.getColumn(segment), type);
    }

    @Override
    protected IResourceLocation calExternalLocation(Segment oneOfSegments) {
        return oneOfSegments.getLocation().getParent().
                buildChildLocation("external_global_dict").
                buildChildLocation(key.getName() + "_" + type);
    }
}