package com.fr.swift.source.db;

import com.fr.swift.source.AbstractOuterDataSource;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.DBDataSource;

import java.util.Map;

/**
 * Created by pony on 2017/11/21.
 */
public abstract class AbstractDBDataSource extends AbstractOuterDataSource implements DBDataSource {
    public AbstractDBDataSource() {
    }

    public AbstractDBDataSource(Map<String, ColumnType> fieldColumnTypes) {
        super(fieldColumnTypes);
    }
}
