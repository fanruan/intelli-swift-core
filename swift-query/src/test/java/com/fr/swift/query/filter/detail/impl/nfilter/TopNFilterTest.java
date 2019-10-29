package com.fr.swift.query.filter.detail.impl.nfilter;


import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.query.filter.detail.DetailFilter;

/**
 * Created by Lyon on 2017/12/5.
 */
public class TopNFilterTest extends BottomNFilterTest {

    public void test() {
        int n = 10;
        DetailFilter filter = new TopNFilter(n, column);
        final MutableBitMap expected = BitMaps.newRoaringMutable();
        int size = column.getDictionaryEncodedColumn().size();
        for (int i = size - n; i < size; i++) {
            expected.or(column.getBitmapIndex().getBitMapIndex(i));
        }
        ImmutableBitMap actual = filter.createFilterIndex();
        assertEquals(expected.getCardinality(), actual.getCardinality());
        actual.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                expected.contains(row);
            }
        });
    }
}
