package com.fr.swift.query.group.impl;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.structure.array.IntListFactory;

/**
 * @author anchore
 * @date 2018/1/31
 * <p>
 * 不分组规则 1个一组
 */
public class NoGroupRule extends BaseGroupRule {
    public NoGroupRule(DictionaryEncodedColumn<?> dictColumn) {
        super(dictColumn);
    }

    @Override
    public String getGroupName(int index) {
        return dictColumn.getValue(index).toString();
    }

    @Override
    public IntList map(int index) {
        IntList list = IntListFactory.createIntList(1);
        list.add(index);
        return list;
    }

    @Override
    public int newSize() {
        return dictColumn.size();
    }
}
