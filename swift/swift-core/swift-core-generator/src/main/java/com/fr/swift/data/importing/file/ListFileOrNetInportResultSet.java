package com.fr.swift.data.importing.file;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-11
 */
public class ListFileOrNetInportResultSet extends BaseFileOrNetImportResultSet<List<String>> {
    public ListFileOrNetInportResultSet(List<String> paths, SwiftSourceAlloter alloter, LineParser parser) {
        super(paths, alloter, parser);
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return false;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return null;
    }

    @Override
    public void close() throws SQLException {

    }
}
