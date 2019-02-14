package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.traversal.TraversalAction;
import com.fr.swift.compare.Comparators;
import com.fr.swift.segment.column.Column;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Lyon on 2017/12/1.
 */
public abstract class BaseFilterTest extends TestCase {

    protected static int rowCount = 10000;
    protected static int keySize = 100;
    protected static Random random = new Random(23854);
    protected static List<Integer> intDetails;
    protected static List<Double> doubleDetails;
    protected static List<Long> longDetails;
    protected static List<String> strDetails;

    protected static Column intColumn;
    protected static Column doubleColumn;
    protected static Column longColumn;
    protected static Column strColumn;

    private static List<Number> numberKeys;

    static {
        initNumberKeys();
        initDetails();
        initColumn();
    }

    protected void check(final List<Integer> expected, ImmutableBitMap actual) {
        assertEquals(expected.size(), actual.getCardinality());
        actual.traversal(new TraversalAction() {
            @Override
            public void actionPerformed(int row) {
                assertTrue(expected.contains(row));
            }
        });
    }

    private static void initColumn() {
        intColumn = new BaseColumnImplTest<Integer>(intDetails, Comparators.<Integer>asc());
        doubleColumn = new BaseColumnImplTest<Double>(doubleDetails, Comparators.<Double>asc());
        longColumn = new BaseColumnImplTest<Long>(longDetails, Comparators.<Long>asc());
        strColumn = new BaseColumnImplTest<String>(strDetails, Comparators.STRING_ASC);
    }

    private static void initNumberKeys() {
        numberKeys = new ArrayList<Number>();
        numberKeys.add(null);
        for (int i = 1; i < keySize; i++) {
            numberKeys.add(i);
        }
    }

    private static void initDetails() {
        intDetails = intDetails(rowCount);
        doubleDetails = doubleDetails(rowCount);
        longDetails = longDetails(rowCount);
        strDetails = createStrDetail(rowCount);
    }

    public static List<Integer> intDetails(int rowCount) {
        List<Integer> intDetails = new ArrayList<Integer>();
        for (int i = 0; i < rowCount; i++) {
            Number val = numberKeys.get(random.nextInt(keySize));
            intDetails.add(val == null ? null : val.intValue());
        }
        return intDetails;
    }

    public static List<Double> doubleDetails(int rowCount) {
        List<Double> doubleDetails = new ArrayList<Double>();
        for (int i = 0; i < rowCount; i++) {
            Number val = numberKeys.get(random.nextInt(keySize));
            doubleDetails.add(val == null ? null : val.doubleValue());
        }
        return doubleDetails;
    }

    public static List<Long> longDetails(int rowCount) {
        List<Long> longDetails = new ArrayList<Long>();
        for (int i = 0; i < rowCount; i++) {
            Number val = numberKeys.get(random.nextInt(keySize));
            longDetails.add(val == null ? null : val.longValue());
        }
        return longDetails;
    }

    public static List<String> createStrDetail(int rowCount) {
        List<String> strKeys = new ArrayList<String>();
        strKeys.add(null);
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder builder = new StringBuilder();
        int maxLen = 10;
        for (int i = 1; i < keySize; i++) {
            for (int j = 0, size = random.nextInt(maxLen); j < size; j++) {
                builder.append(alphabet.charAt(random.nextInt(26)));
            }
            strKeys.add(builder.toString());
            builder.delete(0, builder.length());
        }
        List<String> strDetails = new ArrayList<String>();
        for (int i = 0; i < rowCount; i++) {
            strDetails.add(strKeys.get(random.nextInt(keySize)));
        }
        return strDetails;
    }
}
