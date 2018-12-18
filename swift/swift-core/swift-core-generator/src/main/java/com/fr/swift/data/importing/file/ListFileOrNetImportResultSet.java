package com.fr.swift.data.importing.file;

import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.SwiftSourceAlloter;

import java.sql.SQLException;
import java.util.List;

/**
 * TODO 还没实现
 * @author yee
 * @date 2018-12-11
 */
public class ListFileOrNetImportResultSet extends BaseFileOrNetImportResultSet<List<String>> {
    public ListFileOrNetImportResultSet(List<String> paths, SwiftSourceAlloter alloter, FileLineParser parser) {
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
