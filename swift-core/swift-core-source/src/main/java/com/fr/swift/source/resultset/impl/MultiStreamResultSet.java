package com.fr.swift.source.resultset.impl;

import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.resultset.BaseStreamResultSet;
import com.fr.swift.source.resultset.LineParser;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
public class MultiStreamResultSet extends BaseStreamResultSet<List<String>> {

    private SingleStreamResultSet current;
    private int currentPathIdx = 0;

    public MultiStreamResultSet(SwiftDatabase database, String tableName, List<String> strings, LineParser parser) {
        super(strings, parser);
        this.current = new SingleStreamResultSet(database, tableName, strings.get(0), parser);
    }

    public MultiStreamResultSet(SwiftMetaData metaData, List<String> strings, LineParser parser) {
        super(metaData, strings, parser);
        this.current = new SingleStreamResultSet(metaData, strings.get(0), parser);
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
            current = new SingleStreamResultSet(metaData, paths.get(currentPathIdx), parser);
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
