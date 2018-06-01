package com.fr.swift.query.post;

import com.fr.stable.StringUtils;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.query.Query;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.info.dimension.Dimension;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.NodeResultSetImpl;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lyon on 2018/6/1.
 */
public class ResultJoinQueryTest extends TestCase {

    private List<Query<NodeResultSet>> queries;
    private List<Dimension> dimensions;

    @Override
    protected void setUp() throws Exception {
        AggregatorValue[] values = new AggregatorValue[]{
                new DoubleAmountAggregatorValue(1)
        };
        List<GroupNode> roots = new ArrayList<>();
        roots.add(createNode(new String[]{"a", "b"}, values));
        roots.add(createNode(new String[]{"b", "c"}, values));
        roots.add(createNode(new String[]{"a", "c"}, values));
        List<SwiftMetaData> metaDataList = createMetaData();
        queries = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final int index = i;
            Query<NodeResultSet> query = new PostQuery<NodeResultSet>() {
                @Override
                public NodeResultSet getQueryResult() {
                    return new NodeResultSetImpl(1, roots.get(index), metaDataList.get(index));
                }
            };
            queries.add(query);
        }
        dimensions = new ArrayList<>();
        Dimension dimension = EasyMock.mock(Dimension.class);
        ColumnKey columnKey = EasyMock.mock(ColumnKey.class);
        EasyMock.expect(columnKey.getName()).andReturn("dim").times(1);
        EasyMock.expect(dimension.getColumnKey()).andReturn(columnKey).times(1);
        EasyMock.replay(dimension);
        EasyMock.replay(columnKey);
        dimensions.add(dimension);
    }

    public void test() {
        PostQuery<NodeResultSet> query = new ResultJoinQuery(queries, dimensions);
        try {
            NodeResultSet resultSet = query.getQueryResult();
            assertTrue(resultSet.next());
            Row row = resultSet.getRowData(); // ["a", 1, null, 1]
            assertEquals(row.getSize(), 1 + 3);
            assertTrue(StringUtils.equals(row.getValue(0).toString(), "a"));
            assertEquals(row.getValue(1), 1.);
            assertEquals(row.getValue(2), null);
            assertEquals(row.getValue(3), 1.);

            assertTrue(resultSet.next());
            row = resultSet.getRowData(); // ["b", 1, 1, null]
            assertEquals(row.getSize(), 1 + 3);
            assertTrue(StringUtils.equals(row.getValue(0).toString(), "b"));
            assertEquals(row.getValue(1), 1.);
            assertEquals(row.getValue(2), 1.);
            assertEquals(row.getValue(3), null);

            assertTrue(resultSet.next());
            row = resultSet.getRowData(); // ["c", null, 1, 1]
            assertEquals(row.getSize(), 1 + 3);
            assertTrue(StringUtils.equals(row.getValue(0).toString(), "c"));
            assertEquals(row.getValue(1), null);
            assertEquals(row.getValue(2), 1.);
            assertEquals(row.getValue(3), 1.);

        } catch (SQLException e) {
            assertTrue(false);
        }
    }

    private static List<SwiftMetaData> createMetaData() {
        String[] tableNames = new String[]{"a", "b", "c"};
        String[][] columnNames = new String[][]{
                new String[]{"dim", "a_metric1"},
                new String[]{"dim", "b_metric1"},
                new String[]{"dim", "c_metric1"},
        };
        List<SwiftMetaData> metaData = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            final int index = i;
            metaData.add(new SwiftMetaData() {
                @Override
                public SwiftDatabase.Schema getSwiftSchema() {
                    return null;
                }

                @Override
                public String getSchemaName() {
                    return null;
                }

                @Override
                public String getTableName() {
                    return tableNames[index];
                }

                @Override
                public int getColumnCount() {
                    return columnNames[index].length;
                }

                @Override
                public String getColumnName(int i) {
                    return columnNames[index][i];
                }

                @Override
                public String getColumnRemark(int index) {
                    return null;
                }

                @Override
                public int getColumnType(int index) {
                    return 0;
                }

                @Override
                public int getPrecision(int index) {
                    return 0;
                }

                @Override
                public int getScale(int index) {
                    return 0;
                }

                @Override
                public SwiftMetaDataColumn getColumn(int index) {
                    return null;
                }

                @Override
                public SwiftMetaDataColumn getColumn(String columnName) {
                    return null;
                }

                @Override
                public int getColumnIndex(String columnName) {
                    return 0;
                }

                @Override
                public String getColumnId(int index) {
                    return null;
                }

                @Override
                public String getColumnId(String columnName) {
                    return null;
                }

                @Override
                public String getRemark() {
                    return null;
                }

                @Override
                public List<String> getFieldNames() {
                    return new ArrayList<>(Arrays.asList(columnNames[index]));
                }
            });
        }
        return metaData;
    }

    private static GroupNode createNode(String[] keys, AggregatorValue[] values) {
        GroupNode root = new GroupNode(-1, null);
        for (int i = 0; i < keys.length; i++) {
            GroupNode node = new GroupNode(1, keys[i]);
            node.setAggregatorValue(values);
            root.addChild(node);
        }
        return root;
    }
}
