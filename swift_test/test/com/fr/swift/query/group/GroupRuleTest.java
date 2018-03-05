package com.fr.swift.query.group;

import com.fr.swift.query.group.impl.AutoNumGroupRule;
import com.fr.swift.query.group.impl.AutoNumGroupRule.Partition;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.query.group.impl.CustomStrGroupRule;
import com.fr.swift.query.group.impl.CustomStrGroupRule.StringGroup;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.array.IntList;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author anchore
 * @date 2018/3/1
 */
public class GroupRuleTest extends TestCase {
    public void testCustomStrGroupRule() {
        GroupRule rule = new CustomStrGroupRule(Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2", "3")),
                new StringGroup("g1", Arrays.asList("5", "6"))
        ), "ungrouped");
        rule.setOriginDict(new BaseDictTestColumn<String>() {
            String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index];
            }
        });

        assertEquals(3, rule.newSize());

        assertEquals("g0", rule.getGroupName(0));
        assertEquals("g1", rule.getGroupName(1));
        assertEquals("ungrouped", rule.getGroupName(2));

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

    public void testCustomStrGroupRuleWithNoOtherGroup() {
        GroupRule rule = new CustomStrGroupRule(Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2", "3")),
                new StringGroup("g1", Arrays.asList("5", "6", "7", "8", "9"))
        ), null);
        rule.setOriginDict(new BaseDictTestColumn<String>() {
            String[] values = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index];
            }
        });

        assertEquals(4, rule.newSize());

        assertEquals("g0", rule.getGroupName(0));
        assertEquals("g1", rule.getGroupName(1));
        assertEquals("4", rule.getGroupName(2));
        assertEquals("10", rule.getGroupName(3));

        IntList l0 = rule.map(0);
        assertEquals(3, l0.size());
        assertEquals(0, l0.get(0));
        assertEquals(1, l0.get(1));
        assertEquals(2, l0.get(2));

        IntList l1 = rule.map(1);
        assertEquals(5, l1.size());
        assertEquals(4, l1.get(0));
        assertEquals(5, l1.get(1));
        assertEquals(6, l1.get(2));
        assertEquals(7, l1.get(3));
        assertEquals(8, l1.get(4));

        IntList l2 = rule.map(2);
        assertEquals(1, l2.size());
        assertEquals(3, l2.get(0));

        IntList l3 = rule.map(3);
        assertEquals(1, l3.size());
        assertEquals(9, l3.get(0));
    }

    public void testCustomNumGroupRule() {
        GroupRule rule = new CustomNumGroupRule(Arrays.asList(
                new NumInterval("g0", 0, true, 2.1, false),
                new NumInterval("g1", 4, true, 6, false),
                new NumInterval("g2", 7, true, 10, false)
        ), "ungrouped");
        rule.setOriginDict(new BaseDictTestColumn<Number>() {
            Number[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            @Override
            public int size() {
                return numbers.length;
            }

            @Override
            public Number getValue(int index) {
                return numbers[index];
            }
        });
        assertEquals(4, rule.newSize());

        assertEquals("g0", rule.getGroupName(0));
        assertEquals("g1", rule.getGroupName(1));
        assertEquals("g2", rule.getGroupName(2));
        assertEquals("ungrouped", rule.getGroupName(3));

        IntList l0 = rule.map(0);
        assertEquals(2, l0.size());
        assertEquals(0, l0.get(0));
        assertEquals(1, l0.get(1));

        IntList l1 = rule.map(1);
        assertEquals(2, l1.size());
        assertEquals(3, l1.get(0));
        assertEquals(4, l1.get(1));

        IntList l2 = rule.map(2);
        assertEquals(3, l2.size());
        assertEquals(6, l2.get(0));
        assertEquals(7, l2.get(1));
        assertEquals(8, l2.get(2));

        IntList l3 = rule.map(3);
        assertEquals(3, l3.size());
        assertEquals(2, l3.get(0));
        assertEquals(5, l3.get(1));
        assertEquals(9, l3.get(2));
    }

    public void testCustomNumGroupRuleWithNoOtherGroup() {
        GroupRule rule = new CustomNumGroupRule(Arrays.asList(
                new NumInterval("g0", 0, true, 2.1, false),
                new NumInterval("g1", 4, true, 7, false),
                new NumInterval("g2", 7, true, 10, false)
        ), null);
        rule.setOriginDict(new BaseDictTestColumn<Number>() {
            Number[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            @Override
            public int size() {
                return numbers.length;
            }

            @Override
            public Number getValue(int index) {
                return numbers[index];
            }
        });
        assertEquals(5, rule.newSize());

        assertEquals("g0", rule.getGroupName(0));
        assertEquals("g1", rule.getGroupName(1));
        assertEquals("g2", rule.getGroupName(2));
        assertEquals("3", rule.getGroupName(3));
        assertEquals("10", rule.getGroupName(4));

        IntList l0 = rule.map(0);
        assertEquals(2, l0.size());
        assertEquals(0, l0.get(0));
        assertEquals(1, l0.get(1));

        IntList l1 = rule.map(1);
        assertEquals(3, l1.size());
        assertEquals(3, l1.get(0));
        assertEquals(4, l1.get(1));
        assertEquals(5, l1.get(2));

        IntList l2 = rule.map(2);
        assertEquals(3, l2.size());
        assertEquals(6, l2.get(0));
        assertEquals(7, l2.get(1));
        assertEquals(8, l2.get(2));

        IntList l3 = rule.map(3);
        assertEquals(1, l3.size());
        assertEquals(2, l3.get(0));

        IntList l4 = rule.map(4);
        assertEquals(1, l4.size());
        assertEquals(9, l4.get(0));
    }

    public void testAutoGroupRule() {
        GroupRule rule = new AutoNumGroupRule(new Partition(1, 10, 4));
        rule.setOriginDict(new BaseDictTestColumn<Number>() {
            Number[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            @Override
            public int size() {
                return numbers.length;
            }

            @Override
            public Number getValue(int index) {
                return numbers[index];
            }
        });
        assertEquals(3, rule.newSize());

        assertEquals("1 - 5", rule.getGroupName(0));
        assertEquals("5 - 9", rule.getGroupName(1));
        assertEquals("9 - 10", rule.getGroupName(2));

        IntList l0 = rule.map(0);
        assertEquals(4, l0.size());
        assertEquals(0, l0.get(0));
        assertEquals(1, l0.get(1));
        assertEquals(2, l0.get(2));
        assertEquals(3, l0.get(3));

        IntList l1 = rule.map(1);
        assertEquals(4, l1.size());
        assertEquals(4, l1.get(0));
        assertEquals(5, l1.get(1));
        assertEquals(6, l1.get(2));
        assertEquals(7, l1.get(3));

        IntList l2 = rule.map(2);
        assertEquals(2, l2.size());
        assertEquals(8, l2.get(0));
        assertEquals(9, l2.get(1));
    }

    private static class BaseDictTestColumn<T> implements DictionaryEncodedColumn<T> {
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
}