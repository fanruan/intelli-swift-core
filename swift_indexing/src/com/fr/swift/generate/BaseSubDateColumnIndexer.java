package com.fr.swift.generate;

import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;

/**
 * @author anchore
 * @date 2018/3/23
 */
public abstract class BaseSubDateColumnIndexer<Derive> extends BaseColumnIndexer<Derive> {
    protected GroupType type;

    public BaseSubDateColumnIndexer(DataSource dataSource, ColumnKey key, GroupType type) {
        super(dataSource, key);
        this.type = type;
    }

    @Override
    Column<Derive> getColumn(Segment segment) {
        return transform((Column<Long>) super.getColumn(segment));
    }

    /**
     * 转换一下
     *
     * @param origin 日期源列
     * @return 日期子列
     */
    protected abstract Column<Derive> transform(Column<Long> origin);
}