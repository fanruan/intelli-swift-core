package com.fr.swift.util;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/23.
 */
public class TestArrayLookupHelper extends TestCase {
    private ArrayLookupHelper.Lookup<String> lookup;
    @Override
    public void setUp() throws Exception {
        final String[] sorted = new String[]{"a1", "a2", "a5", "b1", "d3"};
        lookup = new ArrayLookupHelper.Lookup<String>() {
            @Override
            public int minIndex() {
                return 0;
            }

            @Override
            public int maxIndex() {
                return sorted.length - 1;
            }

            @Override
            public String lookupByIndex(int index) {
                return sorted[index];
            }

            @Override
            public int compare(String t1, String t2) {
                return t1.compareTo(t2);
            }
        };
    }

    public void testLookup() throws Exception {
        assertEquals(ArrayLookupHelper.lookup(new String[]{"a1"}, lookup)[0], 0);
        assertEquals(ArrayLookupHelper.lookup(new String[]{"a3"}, lookup)[0], -1);
    }

    public void testGetStartIndex4StartWith() throws Exception {
        assertEquals(ArrayLookupHelper.getStartIndex4StartWith(lookup, "a"), 0);
        assertEquals(ArrayLookupHelper.getStartIndex4StartWith(lookup, "a2"), 1);
        assertEquals(ArrayLookupHelper.getStartIndex4StartWith(lookup, "c"), -1);
    }

    public void testGetEndIndex4StartWith() throws Exception {
        assertEquals(ArrayLookupHelper.getEndIndex4StartWith(lookup, "a"), 2);
        assertEquals(ArrayLookupHelper.getEndIndex4StartWith(lookup, "a2"), 1);
        assertEquals(ArrayLookupHelper.getEndIndex4StartWith(lookup, "c"), -1);
    }

    public void testBinarySearch() throws Exception {
        assertEquals(ArrayLookupHelper.binarySearch(lookup, "a3"), new MatchAndIndex(false, 1));
        assertEquals(ArrayLookupHelper.binarySearch(lookup, "b1"), new MatchAndIndex(true, 3));
    }

}