//package com.fr.swift.query.post;
//
//import com.fr.stable.StringUtils;
//import com.fr.swift.query.filter.detail.DetailFilter;
//import com.fr.swift.query.filter.detail.impl.number.NumberInRangeFilter;
//import com.fr.swift.query.filter.match.MatchFilter;
//import com.fr.swift.result.GroupNode;
//import com.fr.swift.result.NodeMergeResultSetImpl;
//import com.fr.swift.result.NodeResultSet;
//import com.fr.swift.result.SwiftNode;
//import com.fr.swift.source.Row;
//import com.fr.swift.structure.Pair;
//import junit.framework.TestCase;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Lyon on 2018/6/4.
// */
//public class HavingFilterQueryTest extends TestCase {
//
//    PostQuery<NodeResultSet> query;
//
//    @Override
//    public void setUp() throws Exception {
//        GroupNode root = PostQueryTestUtils.createNode(2, new Pair[]{
//                Pair.of(new Object[]{"b", "bb1"}, new int[]{1}),
//                Pair.of(new Object[]{"b", "bb2"}, new int[]{2}),
//                Pair.of(new Object[]{"c", "cc"}, new int[]{1}),
//        });
//
//        Map<Integer, Object> map = new HashMap<>();
//        map.put(1, "b");
//        map.put(2, "c");
//        List<Map<Integer, Object>> maps = new ArrayList<>();
//        maps.add(map);
//        map = new HashMap<>();
//        map.put(1, "bb1");
//        map.put(2, "bb2");
//        map.put(3, "cc");
//        maps.add(map);
//        PostQuery<NodeResultSet> postQuery = new PostQuery<NodeResultSet>() {
//            @Override
//            public NodeResultSet getQueryResult() {
//                return new NodeMergeResultSetImpl(200, root, maps);
//            }
//        };
//
//        List<MatchFilter> matchFilters = new ArrayList<>();
//        matchFilters.add(null);
//        // > 1
//        DetailFilter detailFilter = new NumberInRangeFilter(1., Double.POSITIVE_INFINITY, false, true, null, 0);
//        matchFilters.add(new MatchFilter() {
//            @Override
//            public boolean matches(SwiftNode node) {
//                return detailFilter.matches(node, 0, null);
//            }
//        });
//
//        query = new HavingFilterQuery(postQuery, matchFilters);
//    }
//
//    public void test() {
//        try {
//            NodeResultSet resultSet = query.getQueryResult();
//            assertTrue(resultSet.hasNext());
//            Row row = resultSet.getNextRow();   // ["b", "bb2", 2]
//            assertEquals(row.getSize(), 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "b"));
//            assertEquals(row.getValue(1), "bb2");
//            assertEquals(row.getValue(2), 2.);
//
//            assertFalse(resultSet.hasNext());
//        } catch (SQLException e) {
//            assertTrue(false);
//        }
//    }
//}
