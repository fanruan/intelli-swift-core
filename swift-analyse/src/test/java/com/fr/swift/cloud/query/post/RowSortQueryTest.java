package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.query.sort.DescSort;
import com.fr.swift.cloud.query.sort.Sort;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.NodeQueryResultSetImpl;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lyon on 2019/1/11.
 */
public class RowSortQueryTest {

    private List<Row> expected;
    private RowSortQuery query;

    @Before
    public void setUp() throws Exception {
        GroupNode root = new GroupNode();
        expected = new ArrayList<Row>();
        String[] dims = new String[]{"a", "b", "c", "d"};
        for (int i = 0; i < dims.length; i++) {
            expected.add(new ListBasedRow(Arrays.asList(dims[i], (double) i)));
            GroupNode child = new GroupNode(0, dims[i]);
            child.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(i)});
            root.addChild(child);
        }
        Collections.reverse(expected);
        QueryResultSet<SwiftNode> rs = new NodeQueryResultSetImpl(200, root);
        Query<QueryResultSet<SwiftNode>> postQuery = EasyMock.createMock(Query.class);
        EasyMock.expect(postQuery.getQueryResult()).andReturn(rs).anyTimes();
        EasyMock.replay(postQuery);
        query = new RowSortQuery(postQuery, Collections.<Sort>singletonList(new DescSort(1)));
    }

    @Test
    public void getQueryResult() throws SQLException {
        QueryResultSet resultSet = query.getQueryResult();
        assertTrue(resultSet.hasNextPage());
        try {
            resultSet.getPage();
            fail();
        } catch (Exception ignored) {
        }
        SwiftResultSet swiftResultSet = resultSet.convert(new SwiftMetaDataEntity());
        assertTrue(swiftResultSet.hasNext());
        List<Row> rows = new ArrayList<Row>();
        while (swiftResultSet.hasNext()) {
            rows.add(swiftResultSet.getNextRow());
        }
        assertTrue(rows.size() > 0);
        assertEquals(expected, rows);
    }
}