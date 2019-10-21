package com.fr.swift.segment.backup;

import com.fr.swift.result.DecorateResultSet;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.Row;
import com.fr.swift.source.resultset.IterableResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2019/3/8
 * <p>
 * 可重复遍历，{@link #reuse()}
 */
public class ReusableResultSet extends DecorateResultSet {

    private List<Row> rows = new ArrayList<Row>();

    public ReusableResultSet(SwiftResultSet originResultSet) throws SQLException {
        super(originResultSet);

        toList(originResultSet);

        origin = reuse();
    }

    private void toList(SwiftResultSet resultSet) throws SQLException {
        try {
            while (resultSet.hasNext()) {
                rows.add(resultSet.getNextRow());
            }
        } finally {
            resultSet.close();
        }
    }

    public SwiftResultSet reuse() throws SQLException {
        return new IterableResultSet(rows, getMetaData(), getFetchSize());
    }
}