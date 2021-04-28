package com.fr.swift.cloud.segment.backup;

import com.fr.swift.cloud.result.DecorateResultSet;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.source.resultset.IterableResultSet;

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