package com.fr.swift.query.group.by2.row;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.index.TestCubeData;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.RowTraversal;
import com.fr.swift.test.Preparer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Lyon on 2018/5/7.
 */
public class MultiGroupByRowIteratorTest {

    private Iterator<GroupByEntry[]> iterator;
    private List<Pair<Column, IndexInfo>> dimensions;
    private Map<RowIndexKey<Object[]>, ImmutableBitMap> expected;

    @Before
    public void setUp() throws Exception {
        Preparer.prepareCubeBuild(getClass());
        DataSource dataSource = new QueryDBSource("select * from DEMO_CONTRACT", "test");
        TestCubeData.prepare(dataSource, MultiGroupByRowIteratorTest.class);
        Segment segment = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class)
                .getSegment(dataSource.getSourceKey()).get(0);
        dimensions = TestCubeData.getDimensions(segment, Arrays.asList("合同类型", "合同付款类型"));
        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, new AllShowDetailFilter(segment), new ArrayList<>(), null);
        iterator = new MultiGroupByRowIterator(groupByInfo);
        expected = TestCubeData.groupBy(Arrays.asList(
                segment.getColumn(new ColumnKey("合同类型")).getDetailColumn(),
                segment.getColumn(new ColumnKey("合同付款类型")).getDetailColumn()),
                segment.getAllShowIndex());
    }

    @Test
    public void test() {
        List<GroupByEntry[]> rows = IteratorUtils.iterator2List(iterator);
        assertEquals(expected.size(), rows.size());
        for (GroupByEntry[] row : rows) {
            RowIndexKey<Object[]> key = getKey(row);
            ImmutableBitMap exp = expected.get(key);
            assertNotNull(exp);
            checkBitMap(exp, row[row.length - 1].getTraversal());
        }
    }

    private void checkBitMap(ImmutableBitMap expected, RowTraversal actual) {
        assertEquals(expected.getCardinality(), actual.getCardinality());
        actual.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                assertTrue(expected.contains(row));
            }
        });
    }

    private RowIndexKey<Object[]> getKey(GroupByEntry[] row) {
        Object[] key = new Object[row.length];
        for (int i = 0; i < row.length; i++) {
            key[i] = dimensions.get(i).getKey().getDictionaryEncodedColumn().getValue(row[i].getIndex());
        }
        return new RowIndexKey<>(key);
    }
}
