//package com.fr.swift.query.post;
//
//import com.fr.stable.StringUtils;
//import com.fr.swift.query.info.bean.type.cal.CalTargetType;
//import com.fr.swift.query.info.element.target.GroupTarget;
//import com.fr.swift.query.info.element.target.cal.GroupTargetImpl;
//import com.fr.swift.result.GroupNode;
//import com.fr.swift.result.NodeMergeResultSetImpl;
//import com.fr.swift.result.NodeResultSet;
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
//public class FieldCalQueryTest extends TestCase {
//
//    private PostQuery<NodeResultSet> query;
//
//    @Override
//    public void setUp() throws Exception {
//        GroupNode root = PostQueryTestUtils.createNode(1, new Pair[]{
//                Pair.of(new Object[]{"b"}, new int[]{1, 0}),
//                Pair.of(new Object[]{"c"}, new int[]{2, 0}),
//        });
//        List<GroupTarget> targetList = new ArrayList<>();
//        targetList.add(new GroupTargetImpl(0, 1, new int[]{0}, CalTargetType.ALL_MAX));
//
//        Map<Integer, Object> map = new HashMap<>();
//        map.put(1, "b");
//        map.put(2, "c");
//        List<Map<Integer, Object>> maps = new ArrayList<>();
//        maps.add(map);
//        PostQuery<NodeResultSet> postQuery = new PostQuery<NodeResultSet>() {
//            @Override
//            public NodeResultSet getQueryResult() {
//                return new NodeMergeResultSetImpl(200, root, maps);
//            }
//        };
//        query = new FieldCalQuery(postQuery, targetList);
//    }
//
//    public void test() {
//        try {
//            NodeResultSet resultSet = query.getQueryResult();
//            assertTrue(resultSet.hasNext());
//            Row row = resultSet.getNextRow();   // ["b", 1, 2]
//            assertEquals(row.getSize(), 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "b"));
//            assertEquals(row.getValue(1), 1.);
//            assertEquals(row.getValue(2), 2.);
//
//            assertTrue(resultSet.hasNext());
//            row = resultSet.getNextRow();   // ["c", 2, 2]
//            assertEquals(row.getSize(), 3);
//            assertTrue(StringUtils.equals(row.getValue(0).toString(), "c"));
//            assertEquals(row.getValue(1), 2.);
//            assertEquals(row.getValue(2), 2.);
//
//            assertFalse(resultSet.hasNext());
//        } catch (SQLException e) {
//            assertTrue(false);
//        }
//    }
//}
