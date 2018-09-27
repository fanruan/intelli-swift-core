package com.fr.swift.segment.merge;

import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.resultset.CoSwiftResultSet;
import com.fr.swift.source.resultset.IterableResultSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test() throws SQLException {
        List<Row> rows = Arrays.asList(
                new ListBasedRow(Collections.singletonList(0)),
                new ListBasedRow(Collections.singletonList(1)),
                new ListBasedRow(Collections.singletonList(2)),
                new ListBasedRow(Collections.singletonList(3)),
                new ListBasedRow(Collections.singletonList(4))
        );
        List<SwiftResultSet> resultSets = Arrays.asList(
                new IterableResultSet(Collections.emptyList(), null),
                new IterableResultSet(rows.subList(0, 3), null),
                new IterableResultSet(Collections.emptyList(), null),
                new IterableResultSet(rows.subList(3, 3), null),
                new IterableResultSet(rows.subList(3, 5), null),
                new IterableResultSet(Collections.emptyList(), null)
        );

        SwiftResultSet resultSet = new CoSwiftResultSet(resultSets);
        int i = 0;
        while (resultSet.hasNext()) {
            Row row = resultSet.getNextRow();
            Assert.assertEquals(i++, row.<Object>getValue(0));
        }
        Assert.assertEquals(5, i);
    }
}