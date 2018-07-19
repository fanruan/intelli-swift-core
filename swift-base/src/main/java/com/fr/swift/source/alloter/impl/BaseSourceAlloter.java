package com.fr.swift.source.alloter.impl;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

/**
 * @author anchore
 * @date 2018/6/5
 */
public abstract class BaseSourceAlloter implements SwiftSourceAlloter {
    private SourceKey tableKey;

    public BaseSourceAlloter(SourceKey tableKey) {
        this.tableKey = tableKey;
    }
}