package com.fr.swift.query.filter.detail.impl.util;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.util.ArrayLookupHelper;

/**
 * Created by Lyon on 2017/11/29.
 */
public class LookupFactory {
    public static <T> ArrayLookupHelper.Lookup<T> create(final DictionaryEncodedColumn<T> dict) {
        return new ArrayLookupHelper.Lookup<T>() {
            @Override
            public int minIndex() {
                return 0;
            }

            @Override
            public int maxIndex() {
                return dict.size() - 1;
            }

            @Override
            public T lookupByIndex(int index) {
                return dict.getValue(index);
            }

            @Override
            public int compare(T t1, T t2) {
                return dict.getComparator().compare(t1, t2);
            }
        };
    }
}
