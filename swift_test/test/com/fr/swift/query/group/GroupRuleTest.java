package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.CustomGroupRule;
import com.fr.swift.query.group.impl.CustomGroupRule.StringGroup;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Comparator;

import static com.fr.swift.query.group.impl.CustomGroupRule.UNGROUPED;

/**
 * @author anchore
 * @date 2018/3/1
 */
public class GroupRuleTest extends TestCase {
    public void testCustomGroupRule() {
        GroupRule rule = new CustomGroupRule(new BaseDictTestColumn<String>() {
            Object[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index].toString();
            }
        }, Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2", "3")),
                new StringGroup("g1", Arrays.asList("5", "6")),
                new StringGroup(UNGROUPED, Arrays.asList("4", "7", "8", "9", "10"))
        ), false);

        assertEquals(3, rule.newSize());

        assertEquals("g0", rule.getGroupName(0));
        assertEquals("g1", rule.getGroupName(1));
        assertEquals(UNGROUPED, rule.getGroupName(2));

        assertEquals(0, rule.map(0).get(0));
        assertEquals(1, rule.map(0).get(1));
        assertEquals(2, rule.map(0).get(2));

        assertEquals(4, rule.map(1).get(0));
        assertEquals(5, rule.map(1).get(1));

        assertEquals(3, rule.map(2).get(0));
        assertEquals(6, rule.map(2).get(1));
        assertEquals(7, rule.map(2).get(2));
        assertEquals(8, rule.map(2).get(3));
        assertEquals(9, rule.map(2).get(4));
    }

    public void testCustomNumGroupRule() {
        GroupRule rule = new CustomNumGroupRule(new BaseDictTestColumn<Number>() {
            @Override
            public int size() {

            }

            @Override
            public Number getValue(int index) {

            }
        }, Arrays.asList(
                new NumInterval("g0", 0, true, 2.1, false),
                new NumInterval("g0", 2.1, true, 2.1, false),
                new NumInterval("g0", 0, true, 2.1, false)
        ));
    }
}

class BaseDictTestColumn<T> implements DictionaryEncodedColumn<T> {

    @Override
    public void putSize(int size) {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void putGlobalSize(int globalSize) {

    }

    @Override
    public int globalSize() {
        return 0;
    }

    @Override
    public void putValue(int index, T val) {

    }

    @Override
    public T getValue(int index) {
        return null;
    }

    @Override
    public int getIndex(Object value) {
        return 0;
    }

    @Override
    public void putIndex(int row, int index) {

    }

    @Override
    public int getIndexByRow(int row) {
        return 0;
    }

    @Override
    public void putGlobalIndex(int index, int globalIndex) {

    }

    @Override
    public int getGlobalIndexByIndex(int index) {
        return 0;
    }

    @Override
    public int getGlobalIndexByRow(int row) {
        return 0;
    }

    @Override
    public Comparator<T> getComparator() {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public void release() {

    }
}