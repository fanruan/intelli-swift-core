package com.fr.bi.stable.structure.collection.list;

import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/3.
 */
public abstract class BIArrayListTest<T> extends TestCase {
    BIArrayList list;

    protected abstract BIArrayList<T> getList();

    abstract T getRandomValue();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        list = getList();
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
    }

    public void testGetCorrectness() {
        int size = list.size();
        T value = getRandomValue();
        list.add(value);
        assertEquals(list.get(size), value);
        try {
            list.get(size + 1);
        } catch (Exception e) {
            assertEquals(e.getClass(), ArrayIndexOutOfBoundsException.class);
        }
        value = getRandomValue();
        list.clear();
        list.add(value);
        assertEquals(list.get(0), value);
    }

    public void testSetTwoPara() {
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        T value = getRandomValue();
        list.set(2, value);
        assertEquals(value, list.get(2));
        value = getRandomValue();
        list.set(1, value);
        assertEquals(value, list.get(1));
        value = getRandomValue();
        list.set(0, value);
        assertEquals(value, list.get(0));
        list.clear();
        assertEquals(0, list.size());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        list.add(getRandomValue());
        value = getRandomValue();
        list.set(2, value);
        assertEquals(value, list.get(2));
        value = getRandomValue();
        list.set(1, value);
        assertEquals(value, list.get(1));
        value = getRandomValue();
        list.set(0, value);
        assertEquals(value, list.get(0));
    }

    public void testAddOnePara() {
        int size = list.size();
        T value = getRandomValue();
        list.add(value);
        assertEquals(list.size(), size + 1);
        clear();
        list.add(value);
        assertEquals(list.size(), 1);
    }

    private void clear() {
        list.clear();
        assertEquals(list.size(), 0);
    }

    public void testAddTwoPara() {
        int size = list.size();
        Object[] values = list.toArray();
        T value = getRandomValue();
        list.add(0, value);
        assertEquals(list.size(), size + 1);
        /**
         *
         */
        for (int i = 0; i < values.length; i++) {
            assertEquals(list.get(i + 1), values[i]);
        }
    }

    public void testSize() {
        int size = list.size();
        Object[] values = list.toArray();
        T value = getRandomValue();
        list.add(0, value);
        assertEquals(list.size(), size + 1);
        clear();
    }

    public void testGetLastValue() {
        T value = getRandomValue();
        list.add(value);
        assertEquals(list.getLastValue(), value);
        clear();
    }

    public void testSetLastValue() {
        T value = getRandomValue();
        list.setLastValue(value);
        assertEquals(list.getLastValue(), value);
        clear();
    }

    public void testContains() {
        int count = 1;
        while (count++ < 100) {
            T value = getRandomValue();
            list.set(Math.abs(BIRandomUitils.getRandomInteger()) % list.size(), value);
            assertTrue(list.contains(value));
        }
        clear();
    }


    public void testIndexOf() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();
        list.add(value1);
        list.add(value2);
        list.add(value3);
        assertEquals(list.indexOf(value1), 0);
        assertEquals(list.indexOf(value2), 1);
        assertEquals(list.indexOf(value3), 2);

        clear();
    }

    public void testRemove() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.remove(0);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), value2);
        assertEquals(list.get(1), value3);
        clear();
        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.remove(1);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), value1);
        assertEquals(list.get(1), value3);
        clear();
    }

    public void testSubList() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();

        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.subList(2);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), value1);
        assertEquals(list.get(1), value2);
    }


    public void testRemoveEqual() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();

        list.add(value1);
        list.add(value1);
        list.add(value1);
        list.add(value1);
        list.add(value1);

        assertEquals(list.size(), 5);
        list.removeEqual();
        assertEquals(list.size(), 1);
        list.clear();
        list.add(value1);
        list.add(value2);
        list.add(value1);
        list.add(value2);
        list.add(value1);
        list.add(value3);
        list.add(value2);
        list.add(value1);
        list.add(value3);
        list.add(value2);
        list.add(value1);
        list.add(value2);
        list.add(value1);
        assertTrue(list.size() != 3);
        list.removeEqual();
        assertTrue(list.size() == 3);
    }

    public void testToArray() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();

        list.add(value1);
        list.add(value2);
        list.add(value3);
        Object[] array = list.toArray();
        for (int i = 0; i < array.length; i++) {
            assertEquals(array[i], list.get(i));
        }

    }

    /**
     * 排序
     */
    public void testSort() {

    }

    /**
     * 清楚整数链表中的所有元素
     */
    public void testClear() {
        clear();

        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();

        list.add(value1);
        list.add(value2);
        list.add(value3);
        assertTrue(list.size() == 3);
        list.clear();
        assertTrue(list.size() == 0);

    }

    public void testTrimToSize() {

    }

    public void testAddAllTwoPara() {

    }

    public void testAddAll() {
        list.clear();
        T value1 = getRandomValue();
        T value2 = getRandomValue();
        T value3 = getRandomValue();

        list.add(value1);
        list.add(value2);
        list.add(value3);
        list.addAll(list);
        assertEquals(list.size(), 6);

        assertEquals(list.get(0), value1);
        assertEquals(list.get(1), value2);
        assertEquals(list.get(2), value3);
        assertEquals(list.get(3), value1);
        assertEquals(list.get(4), value2);
        assertEquals(list.get(5), value3);
    }
}