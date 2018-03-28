package com.fr.swift.query.group.impl;

import com.fr.swift.query.group.GroupRule;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreGenerator;

/**
 * @author anchore
 * @date 2018/1/29
 */
abstract class BaseGroupRule<Base, Derive> implements GroupRule<Base, Derive> {
    DictionaryEncodedColumn<Base> dictColumn;

    /**
     * 初始化映射关系
     */
    abstract void initMap();

    @Override
    public void setOriginDict(DictionaryEncodedColumn<Base> dict) {
        this.dictColumn = dict;
        initMap();
    }

    @Override
    public Core fetchObjectCore() {
        try {
            return new CoreGenerator(this).fetchObjectCore();
        } catch(Exception ignore) {

        }
        return Core.EMPTY_CORE;
    }
}