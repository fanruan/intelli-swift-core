package com.fr.swift.result;

import junit.framework.TestCase;

/**
 * Created by pony on 2017/10/16.
 */
public class TestTargetGettingKey extends TestCase {

    public void testEquals() {
        TargetGettingKey key1 = new TargetGettingKey(1);
        TargetGettingKey key2 = new TargetGettingKey(2);
        TargetGettingKey key3 = new TargetGettingKey(1);
        assertEquals(key1.equals(key3), true);
        assertEquals(key1.equals(key2), false);

    }

    public void testHashCode() {
        TargetGettingKey key1 = new TargetGettingKey(1);
        TargetGettingKey key2 = new TargetGettingKey(2);
        TargetGettingKey key3 = new TargetGettingKey(1);
        assertEquals(key1.hashCode() - key3.hashCode() == 0, true);
        assertEquals(key1.hashCode() - key2.hashCode() == 0, false);
    }

}