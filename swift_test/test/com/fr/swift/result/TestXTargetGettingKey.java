package com.fr.swift.result;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/16.
 */
public class TestXTargetGettingKey extends TestCase{
    public void testEquals() {
        XTargetGettingKey key1 = new XTargetGettingKey(1,1);
        XTargetGettingKey key2 = new XTargetGettingKey(2,2);
        XTargetGettingKey key3 = new XTargetGettingKey(1,1);
        XTargetGettingKey key4 = new XTargetGettingKey(1,2);
        XTargetGettingKey key5 = new XTargetGettingKey(2,1);
        assertEquals(key1.equals(key3), true);
        assertEquals(key1.equals(key2), false);
        assertEquals(key1.equals(key4), false);
        assertEquals(key1.equals(key5), false);
    }

    public void testHashCode() {
        XTargetGettingKey key1 = new XTargetGettingKey(1,1);
        XTargetGettingKey key2 = new XTargetGettingKey(2,2);
        XTargetGettingKey key3 = new XTargetGettingKey(1,1);
        XTargetGettingKey key4 = new XTargetGettingKey(1,2);
        XTargetGettingKey key5 = new XTargetGettingKey(2,1);
        assertEquals(key1.hashCode() - key3.hashCode() == 0, true);
        assertEquals(key1.hashCode() - key2.hashCode() == 0, false);
        assertEquals(key1.hashCode() - key4.hashCode() == 0, false);
        assertEquals(key1.hashCode() - key5.hashCode() == 0, false);
    }

}