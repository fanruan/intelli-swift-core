package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.DictionaryEncodedColumn;

/**
 * @author anchore
 * @date 2018/1/29
 */
abstract class BaseGroupRule implements GroupRule {
    DictionaryEncodedColumn<?> dictColumn;

    BaseGroupRule(DictionaryEncodedColumn<?> dictColumn) {
        this.dictColumn = dictColumn;
    }
}