package com.fr.swift.source.resultset.importing.file;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.List;

/**
 * TODO 还没实现
 * @author yee
 * @date 2018-12-11
 */
public class ListStreamImportResultSet extends BaseStreamImportResultSet<List<String>> {
    private SingleStreamImportResultSet current;
    private int currentPathIdx = 0;

    public ListStreamImportResultSet(SwiftDatabase database, String tableName, List<String> strings, FileLineParser parser) {
        super(strings, parser);
        this.current = new SingleStreamImportResultSet(database, tableName, strings.get(0), parser);
    }

    public ListStreamImportResultSet(SwiftMetaData metaData, List<String> strings, FileLineParser parser) {
        super(metaData, strings, parser);
        this.current = new SingleStreamImportResultSet(metaData, strings.get(0), parser);
    }

    @Override
    public int getFetchSize() {
        return 0;
    }


    @Override
    public boolean hasNext() throws SQLException {
        if (this.current.hasNext()) {
            return true;
        }
        current.close();
        current = null;
        if (++currentPathIdx < paths.size()) {
            current = new SingleStreamImportResultSet(metaData, paths.get(currentPathIdx), parser);
            return current.hasNext();
        }
        return false;
    }

    @Override
    public Row getNextRow() throws SQLException {
        return current.getNextRow();
    }

    @Override
    public void close() throws SQLException {
        current.close();
    }
}
