package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.LongDictColumn;


/**
 * 截取后的日期，如：
 * 截取年月：2017/11/30 12:23:34.456 => 2017/11/01 00:00:00.000
 * 截取年，季度（截取后取当季第一个月的第一天）：2017/11/30 12:23:34.456 => 2017/10/01 00:00:00.000
 *
 * @author anchore
 * @date 2017/12/01
 */
public class TruncatedDateColumn extends BaseSubDateColumn<Long, Long> {
    public TruncatedDateColumn(IResourceLocation location, GroupType type, Column<Long> origin) {
        super(location, type, origin);
    }

    @Override
    public DictionaryEncodedColumn<Long> getDictionaryEncodedColumn() {
        return dictColumn != null ? dictColumn : (dictColumn = new LongDictColumn(location, Comparators.<Long>asc()));
    }
}