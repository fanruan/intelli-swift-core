package com.fr.swift.source.resultset;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author anchore
 * @date 2018/7/27
 */
public class CoSwiftResultSetTest {

    @Test
    public void test() throws SQLException {
        List<Row> rows = Arrays.<Row>asList(
                new ListBasedRow(Collections.<Object>singletonList(0)),
                new ListBasedRow(Collections.<Object>singletonList(1)),
                new ListBasedRow(Collections.<Object>singletonList(2)),
                new ListBasedRow(Collections.<Object>singletonList(3)),
                new ListBasedRow(Collections.<Object>singletonList(4))
        );
        List<SwiftResultSet> resultSets = Arrays.<SwiftResultSet>asList(
                new IterableResultSet(Collections.<Row>emptyList(), null),
                new IterableResultSet(rows.subList(0, 3), null),
                new IterableResultSet(Collections.<Row>emptyList(), null),
                new IterableResultSet(rows.subList(3, 3), null),
                new IterableResultSet(rows.subList(3, 5), null),
                new IterableResultSet(Collections.<Row>emptyList(), null)
        );

        SwiftResultSet resultSet = new CoSwiftResultSet(resultSets);
        int i = 0;
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            Assert.assertEquals(i++, row.getValue(0));
        }
        Assert.assertEquals(5, i);
    }
}