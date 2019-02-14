package com.fr.swift.query.group;

import com.fr.swift.compare.Comparators;
import com.fr.swift.query.group.impl.AutoNumGroupRule;
import com.fr.swift.query.group.impl.AutoNumGroupRule.Partition;
import com.fr.swift.query.group.impl.CustomNumGroupRule;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumInterval;
import com.fr.swift.query.group.impl.CustomNumGroupRule.NumIntervals;
import com.fr.swift.query.group.impl.CustomStrGroupRule;
import com.fr.swift.query.group.impl.CustomStrGroupRule.StringGroup;
import com.fr.swift.structure.array.IntList;
import com.fr.swift.test.Temps.TempDictColumn;
import com.fr.swift.util.Util;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/3/1
 */
public class GroupRuleTest {
    @Test
    public void testCustomStrGroupRule() {
        CustomGroupRule rule = new CustomStrGroupRule(Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2", "3")),
                new StringGroup("g1", Arrays.asList("5", "6"))
        ), "ungrouped", false);
        rule.setOriginDict(new TempDictColumn<String>() {
            String[] values = {null, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index];
            }
        });

        assertEquals(0, rule.getGlobalIndexByIndex(0));
        assertEquals(1, rule.getGlobalIndexByIndex(1));
        assertEquals(2, rule.getGlobalIndexByIndex(2));
        assertEquals(3, rule.getGlobalIndexByIndex(3));

        assertEquals(4, rule.newSize());

        assertEquals("g0", rule.getValue(1));
        assertEquals("g1", rule.getValue(2));
        assertEquals("ungrouped", rule.getValue(3));

        assertEquals(0, rule.getIndex(null));
        assertEquals(1, rule.getIndex("g0"));
        assertEquals(2, rule.getIndex("g1"));
        assertEquals(3, rule.getIndex("ungrouped"));

        assertEquals(1, rule.map(1).get(0));
        assertEquals(2, rule.map(1).get(1));
        assertEquals(3, rule.map(1).get(2));

        assertEquals(5, rule.map(2).get(0));
        assertEquals(6, rule.map(2).get(1));

        assertEquals(4, rule.map(3).get(0));
        assertEquals(7, rule.map(3).get(1));
        assertEquals(8, rule.map(3).get(2));
        assertEquals(9, rule.map(3).get(3));
        assertEquals(10, rule.map(3).get(4));

        assertEquals(0, rule.reverseMap(0).get(0));
        assertEquals(1, rule.reverseMap(1).get(0));
        assertEquals(1, rule.reverseMap(2).get(0));
        assertEquals(1, rule.reverseMap(3).get(0));
        assertEquals(3, rule.reverseMap(4).get(0));
        assertEquals(2, rule.reverseMap(5).get(0));
        assertEquals(2, rule.reverseMap(6).get(0));
        assertEquals(3, rule.reverseMap(7).get(0));
        assertEquals(3, rule.reverseMap(8).get(0));
        assertEquals(3, rule.reverseMap(9).get(0));
        assertEquals(3, rule.reverseMap(10).get(0));
    }

    @Test
    public void testCustomStrGroupRuleWithNoOtherGroup() {
        CustomGroupRule rule = new CustomStrGroupRule(Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2", "3")),
                new StringGroup("g1", Arrays.asList("5", "6", "7", "8", "9"))
        ), null, false);
        rule.setOriginDict(new TempDictColumn<String>() {
            String[] values = {null, "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index];
            }
        });

        assertEquals(5, rule.newSize());

        assertEquals("g0", rule.getValue(1));
        assertEquals("g1", rule.getValue(2));
        assertEquals("4", rule.getValue(3));
        assertEquals("10", rule.getValue(4));

        assertEquals(0, rule.getIndex(null));
        assertEquals(1, rule.getIndex("g0"));
        assertEquals(2, rule.getIndex("g1"));
        assertEquals(3, rule.getIndex("4"));
        assertEquals(4, rule.getIndex("10"));

        IntList l0 = rule.map(1);
        assertEquals(3, l0.size());
        assertEquals(1, l0.get(0));
        assertEquals(2, l0.get(1));
        assertEquals(3, l0.get(2));

        IntList l1 = rule.map(2);
        assertEquals(5, l1.size());
        assertEquals(5, l1.get(0));
        assertEquals(6, l1.get(1));
        assertEquals(7, l1.get(2));
        assertEquals(8, l1.get(3));
        assertEquals(9, l1.get(4));

        IntList l2 = rule.map(3);
        assertEquals(1, l2.size());
        assertEquals(4, l2.get(0));

        IntList l3 = rule.map(4);
        assertEquals(1, l3.size());
        assertEquals(10, l3.get(0));

        assertEquals(0, rule.reverseMap(0).get(0));
        assertEquals(1, rule.reverseMap(1).get(0));
        assertEquals(1, rule.reverseMap(2).get(0));
        assertEquals(1, rule.reverseMap(3).get(0));
        assertEquals(3, rule.reverseMap(4).get(0));
        assertEquals(2, rule.reverseMap(5).get(0));
        assertEquals(2, rule.reverseMap(6).get(0));
        assertEquals(2, rule.reverseMap(7).get(0));
        assertEquals(2, rule.reverseMap(8).get(0));
        assertEquals(2, rule.reverseMap(9).get(0));
        assertEquals(4, rule.reverseMap(10).get(0));
    }

    @Test
    public void testCustomNumGroupRule() {
        CustomGroupRule rule = new CustomNumGroupRule(
                Arrays.asList(
                        new NumIntervals("g0", Collections.singletonList(new NumInterval(0, true, 2.1, false))),
                        new NumIntervals("g1", Collections.singletonList(new NumInterval(4, true, 6, false))),
                        new NumIntervals("g2", Collections.singletonList(new NumInterval(7, true, 10, false)))
                ), "ungrouped", false);
        rule.setOriginDict(new TempDictColumn<Number>() {
            Number[] numbers = {null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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

        assertEquals("g0", rule.getValue(1));
        assertEquals("g1", rule.getValue(2));
        assertEquals("g2", rule.getValue(3));
        assertEquals("ungrouped", rule.getValue(4));

        assertEquals(0, rule.getIndex(null));
        assertEquals(1, rule.getIndex("g0"));
        assertEquals(2, rule.getIndex("g1"));
        assertEquals(3, rule.getIndex("g2"));
        assertEquals(4, rule.getIndex("ungrouped"));

        IntList l0 = rule.map(1);
        assertEquals(2, l0.size());
        assertEquals(1, l0.get(0));
        assertEquals(2, l0.get(1));

        IntList l1 = rule.map(2);
        assertEquals(2, l1.size());
        assertEquals(4, l1.get(0));
        assertEquals(5, l1.get(1));

        IntList l2 = rule.map(3);
        assertEquals(3, l2.size());
        assertEquals(7, l2.get(0));
        assertEquals(8, l2.get(1));
        assertEquals(9, l2.get(2));

        IntList l3 = rule.map(4);
        assertEquals(3, l3.size());
        assertEquals(3, l3.get(0));
        assertEquals(6, l3.get(1));
        assertEquals(10, l3.get(2));

        assertEquals(0, rule.reverseMap(0).get(0));
        assertEquals(1, rule.reverseMap(1).get(0));
        assertEquals(1, rule.reverseMap(2).get(0));
        assertEquals(4, rule.reverseMap(3).get(0));
        assertEquals(2, rule.reverseMap(4).get(0));
        assertEquals(2, rule.reverseMap(5).get(0));
        assertEquals(4, rule.reverseMap(6).get(0));
        assertEquals(3, rule.reverseMap(7).get(0));
        assertEquals(3, rule.reverseMap(8).get(0));
        assertEquals(3, rule.reverseMap(9).get(0));
        assertEquals(4, rule.reverseMap(10).get(0));
    }

    @Test
    public void testCustomNumGroupRuleWithNoOtherGroup() {
        CustomGroupRule rule = new CustomNumGroupRule(
                Arrays.asList(
                        new NumIntervals("g0", Collections.singletonList(new NumInterval(0, true, 2.1, false))),
                        new NumIntervals("g1", Collections.singletonList(new NumInterval(4, true, 7, false))),
                        new NumIntervals("g2", Collections.singletonList(new NumInterval(7, true, 10, false)))
                ), null, false);
        rule.setOriginDict(new TempDictColumn<Number>() {
            Number[] numbers = {null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            @Override
            public int size() {
                return numbers.length;
            }

            @Override
            public Number getValue(int index) {
                return numbers[index];
            }
        });
        assertEquals(6, rule.newSize());

        assertEquals("g0", rule.getValue(1));
        assertEquals("g1", rule.getValue(2));
        assertEquals("g2", rule.getValue(3));
        assertEquals("3", rule.getValue(4));
        assertEquals("10", rule.getValue(5));

        assertEquals(0, rule.getIndex(null));
        assertEquals(1, rule.getIndex("g0"));
        assertEquals(2, rule.getIndex("g1"));
        assertEquals(3, rule.getIndex("g2"));
        assertEquals(4, rule.getIndex("3"));
        assertEquals(5, rule.getIndex("10"));

        IntList l0 = rule.map(1);
        assertEquals(2, l0.size());
        assertEquals(1, l0.get(0));
        assertEquals(2, l0.get(1));

        IntList l1 = rule.map(2);
        assertEquals(3, l1.size());
        assertEquals(4, l1.get(0));
        assertEquals(5, l1.get(1));
        assertEquals(6, l1.get(2));

        IntList l2 = rule.map(3);
        assertEquals(3, l2.size());
        assertEquals(7, l2.get(0));
        assertEquals(8, l2.get(1));
        assertEquals(9, l2.get(2));

        IntList l3 = rule.map(4);
        assertEquals(1, l3.size());
        assertEquals(3, l3.get(0));

        IntList l4 = rule.map(5);
        assertEquals(1, l4.size());
        assertEquals(10, l4.get(0));

        assertEquals(0, rule.reverseMap(0).get(0));
        assertEquals(1, rule.reverseMap(1).get(0));
        assertEquals(1, rule.reverseMap(2).get(0));
        assertEquals(4, rule.reverseMap(3).get(0));
        assertEquals(2, rule.reverseMap(4).get(0));
        assertEquals(2, rule.reverseMap(5).get(0));
        assertEquals(2, rule.reverseMap(6).get(0));
        assertEquals(3, rule.reverseMap(7).get(0));
        assertEquals(3, rule.reverseMap(8).get(0));
        assertEquals(3, rule.reverseMap(9).get(0));
        assertEquals(5, rule.reverseMap(10).get(0));
    }

    @Test
    public void testAutoGroupRule() {
        CustomGroupRule rule = new AutoNumGroupRule(new Partition(1, 10, 4), false);
        rule.setOriginDict(new TempDictColumn<Number>() {
            Number[] numbers = {null, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

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

        assertEquals("1 - 5", rule.getValue(1));
        assertEquals("5 - 9", rule.getValue(2));
        assertEquals("9 - 10", rule.getValue(3));

        assertEquals(0, rule.getIndex(null));
        assertEquals(1, rule.getIndex("1 - 5"));
        assertEquals(2, rule.getIndex("5 - 9"));
        assertEquals(3, rule.getIndex("9 - 10"));

        IntList l0 = rule.map(1);
        assertEquals(4, l0.size());
        assertEquals(1, l0.get(0));
        assertEquals(2, l0.get(1));
        assertEquals(3, l0.get(2));
        assertEquals(4, l0.get(3));

        IntList l1 = rule.map(2);
        assertEquals(4, l1.size());
        assertEquals(5, l1.get(0));
        assertEquals(6, l1.get(1));
        assertEquals(7, l1.get(2));
        assertEquals(8, l1.get(3));

        IntList l2 = rule.map(3);
        assertEquals(2, l2.size());
        assertEquals(9, l2.get(0));
        assertEquals(10, l2.get(1));

        assertEquals(0, rule.reverseMap(0).get(0));
        assertEquals(1, rule.reverseMap(1).get(0));
        assertEquals(1, rule.reverseMap(2).get(0));
        assertEquals(1, rule.reverseMap(3).get(0));
        assertEquals(1, rule.reverseMap(4).get(0));
        assertEquals(2, rule.reverseMap(5).get(0));
        assertEquals(2, rule.reverseMap(6).get(0));
        assertEquals(2, rule.reverseMap(7).get(0));
        assertEquals(2, rule.reverseMap(8).get(0));
        assertEquals(3, rule.reverseMap(9).get(0));
        assertEquals(3, rule.reverseMap(10).get(0));
    }

    @Test
    public void testNumGlobalIndex() {
        CustomGroupRule rule = new CustomNumGroupRule(
                Arrays.asList(
                        new NumIntervals("g0", Collections.singletonList(new NumInterval(0, true, 2.1, false))),
                        new NumIntervals("g1", Collections.singletonList(new NumInterval(4, true, 6, false))),
                        new NumIntervals("g2", Collections.singletonList(new NumInterval(7, true, 10, false)))
                ), null, false);
        rule.setOriginDict(new TempDictColumn<Number>() {
            Number[] numbers = {null, 3.0, 6.1};

            @Override
            public int size() {
                return numbers.length;
            }

            @Override
            public Number getValue(int index) {
                return numbers[index];
            }

            @Override
            public int getIndex(Object value) {
                for (int i = 0; i < numbers.length; i++) {
                    if (Util.equals(numbers[i], value)) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return index;
            }
        });

        assertEquals(0, rule.getGlobalIndexByIndex(0));
        assertEquals(4, rule.getGlobalIndexByIndex(1));
        assertEquals(5, rule.getGlobalIndexByIndex(2));
    }

    @Test
    public void testStrGlobalIndex() {
        CustomGroupRule rule = new CustomStrGroupRule(Arrays.asList(
                new StringGroup("g0", Arrays.asList("1", "2")),
                new StringGroup("g1", Arrays.asList("3", "4", "6", "7")),
                new StringGroup("g2", Arrays.asList("8", "9"))
        ), null, false);
        rule.setOriginDict(new TempDictColumn<String>() {
            String[] values = {null, "5"};
            int[] globalIndices = {0, 5};

            @Override
            public int size() {
                return values.length;
            }

            @Override
            public String getValue(int index) {
                return values[index];
            }

            @Override
            public int getIndex(Object value) {
                for (int i = 0; i < values.length; i++) {
                    if (Util.equals(values[i], value)) {
                        return i;
                    }
                }
                return -1;
            }

            @Override
            public int getGlobalIndexByIndex(int index) {
                return globalIndices[index];
            }

            @Override
            public Comparator<String> getComparator() {
                return Comparators.STRING_ASC;
            }
        });

        assertEquals(0, rule.getGlobalIndexByIndex(0));
        assertEquals(4, rule.getGlobalIndexByIndex(1));
    }
}