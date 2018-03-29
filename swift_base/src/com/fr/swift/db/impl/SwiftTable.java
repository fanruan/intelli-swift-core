package com.fr.swift.db.impl;

import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;

import java.sql.SQLException;

/**
 * @author anchore
 * @date 2018/3/28
 */
public class SwiftTable implements Table {
    private SwiftMetaData meta;

    public SwiftTable(SwiftMetaData meta) {
        this.meta = meta;
    }

    @Override
    public SwiftMetaData getMeta() {
        return meta;
    }

    @Override
    public void setMeta(SwiftMetaData meta) {
        this.meta = meta;
    }

    @Override
    public void insert(SwiftResultSet rowSet) throws SQLException {
        Inserter inserter = null;
        try {
            inserter.insertData(rowSet);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public int delete(Where where) {
        return 0;
    }

    @Override
    public int update(Where where, SwiftResultSet rowSet) {
        return 0;
    }

    @Override
    public SwiftResultSet select(Where where) {
        return null;
    }
}