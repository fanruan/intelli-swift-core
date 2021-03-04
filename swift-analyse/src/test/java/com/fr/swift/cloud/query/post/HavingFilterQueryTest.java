package com.fr.swift.cloud.query.post;

import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.cloud.query.filter.detail.impl.number.NumberInRangeFilter;
import com.fr.swift.cloud.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.cloud.query.filter.match.MatchFilter;
import com.fr.swift.cloud.query.query.Query;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.NodeQueryResultSetImpl;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.ListBasedRow;
import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.structure.iterator.IteratorUtils;
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

/**
 * Created by lyon on 2019/1/11.
 */
public class HavingFilterQueryTest {

    private List<Row> expected;
    private HavingFilterQuery query;

    @Before
    public void setUp() throws Exception {
        GroupNode root = new GroupNode();
        expected = new ArrayList<Row>();
        String[] dims = new String[]{"a", "b", "c", "d"};
        for (int i = 0; i < dims.length; i++) {
            if (i > 0 && i < 3) {
                expected.add(new ListBasedRow(Arrays.asList(dims[i], (double) i)));
            }
            GroupNode child = new GroupNode(0, dims[i]);
            child.setAggregatorValue(new AggregatorValue[]{new DoubleAmountAggregatorValue(i)});
            root.addChild(child);
        }
        QueryResultSet<SwiftNode> rs = new NodeQueryResultSetImpl(200, root);
        Query<QueryResultSet<SwiftNode>> postQuery = EasyMock.createMock(Query.class);
        EasyMock.expect(postQuery.getQueryResult()).andReturn(rs).anyTimes();
        EasyMock.replay(postQuery);
        DetailBasedMatchFilter filter = new DetailBasedMatchFilter(0,
                new NumberInRangeFilter(0, 3, false, false, null, 0));
        query = new HavingFilterQuery(postQuery, Collections.<MatchFilter>singletonList(filter));
    }

    @Test
    public void getQueryResult() throws SQLException {
        QueryResultSet<SwiftNode> resultSet = query.getQueryResult();
        assertTrue(resultSet.hasNextPage());
        List<Row> rows = new ArrayList<Row>();
        while (resultSet.hasNextPage()) {
            rows.addAll(IteratorUtils.iterator2List(SwiftNodeUtils.node2RowIterator(resultSet.getPage())));
        }
        assertTrue(rows.size() > 0);
        assertEquals(expected, rows);
        resultSet.convert(new SwiftMetaDataEntity());
    }
}