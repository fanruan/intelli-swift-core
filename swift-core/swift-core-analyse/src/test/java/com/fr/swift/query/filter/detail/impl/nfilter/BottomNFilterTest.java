package com.fr.swift.query.filter.detail.impl.nfilter;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.BaseColumnImplTest;
import com.fr.swift.query.filter.detail.impl.BaseFilterTest;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.test.Temps;

import java.util.Comparator;

/**
 * Created by Lyon on 2017/12/5.
 */
public class BottomNFilterTest extends BaseFilterTest {

    protected Column column;

    @Override
    public void setUp() throws Exception {
        column = new BaseColumnImplTest<String>(strDetails, Comparators.STRING_ASC) {
            @Override
            public DictionaryEncodedColumn getDictionaryEncodedColumn() {
                final DictionaryEncodedColumn dict = super.getDictionaryEncodedColumn();
                return new Temps.TempDictColumn() {

                    @Override
                    public int size() {
                        return dict.size();
                    }

                    @Override
                    public Object getValue(int index) {
                        return dict.getValue(index);
                    }

                    @Override
                    public int getIndex(Object value) {
                        return dict.getIndex(value);
                    }

                    @Override
                    public Comparator getComparator() {
                        return dict.getComparator();
                    }

                    @Override
                    public int globalSize() {
                        return size();
                    }

                    @Override
                    public int getGlobalIndexByIndex(int index) {
                        return index;
                    }
                };
            }
        };
    }

    public void test() {
        int n = 10;
        DetailFilter filter = new BottomNFilter(n, column);
        final MutableBitMap expected = BitMaps.newRoaringMutable();
        for (int i = 1; i <= n; i++) {
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
