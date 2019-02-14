package com.fr.swift.query.group.by2.row;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.group.by.CubeData;
import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.GroupByInfoImpl;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.query.sort.Sort;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.RowTraversal;
import junit.framework.TestCase;
import org.easymock.EasyMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/7.
 */
public class MultiGroupByRowIteratorTest extends TestCase {

    private Iterator<GroupByEntry[]> iterator;
    private List<Pair<Column, IndexInfo>> dimensions;
    private Map<List<String>, MutableBitMap> expected;
    private int rowCount = 10000;

    @Override
    public void setUp() throws Exception {
        CubeData cubeData = new CubeData(2, 1, rowCount);
        dimensions = cubeData.getDimensions();
        DetailFilter filter = EasyMock.createMock(DetailFilter.class);
        EasyMock.expect(filter.createFilterIndex()).andReturn(BitMaps.newAllShowBitMap(rowCount)).anyTimes();
        EasyMock.replay(filter);
        GroupByInfo groupByInfo = new GroupByInfoImpl(Integer.MAX_VALUE, dimensions, filter, new ArrayList<Sort>(), null);
        iterator = new MultiGroupByRowIterator(groupByInfo);
        prepareExpected();
    }

    private void prepareExpected() {
        expected = new HashMap<List<String>, MutableBitMap>();
        for (int i = 0; i < rowCount; i++) {
            List<String> keys = new ArrayList<String>();
            for (int j = 0; j < dimensions.size(); j++) {
                String value = (String) dimensions.get(j).getKey().getDetailColumn().get(i);
                keys.add(value);
            }
            MutableBitMap bitMap = expected.get(keys);
            if (bitMap == null) {
                bitMap = BitMaps.newRoaringMutable();
                expected.put(keys, bitMap);
            }
            bitMap.add(i);
        }
    }

    public void test() {
        assertTrue(iterator.hasNext());
        while (iterator.hasNext()) {
            GroupByEntry[] row = iterator.next();
            List<String> key = getKey(row);
            assertTrue(expected.containsKey(key));
            checkBitMap(expected.get(key), row[row.length - 1].getTraversal());
        }
    }

    private void checkBitMap(final ImmutableBitMap expected, RowTraversal actual) {
        assertEquals(expected.getCardinality(), actual.getCardinality());
        actual.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                assertTrue(expected.contains(row));
            }
        });
    }

    private List<String> getKey(GroupByEntry[] row) {
        List<String> key = new ArrayList<String>();
        for (int i = 0; i < row.length; i++) {
            key.add((String) dimensions.get(i).getKey().getDictionaryEncodedColumn().getValue(row[i].getIndex()));
        }
        return key;
    }
}
