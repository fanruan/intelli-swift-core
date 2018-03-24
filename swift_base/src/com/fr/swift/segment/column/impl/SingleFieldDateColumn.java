package com.fr.swift.segment.column.impl;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.base.IntDictColumn;

/**
 * 日期子字段，如：
 * 年、月、日、时、分、秒、毫秒
 * 季度、全年第几周等
 *
 * @author anchore
 * @date 2017/12/1
 */
public class SingleFieldDateColumn extends BaseSubDateColumn<Integer> {
    public SingleFieldDateColumn(Column<Long> origin, GroupType type) {
        super(origin, type);
    }

    @Override
    public DictionaryEncodedColumn<Integer> getDictionaryEncodedColumn() {
        return dictColumn != null ? dictColumn : (dictColumn = new IntDictColumn(location, Comparators.<Integer>asc()));
    }
}