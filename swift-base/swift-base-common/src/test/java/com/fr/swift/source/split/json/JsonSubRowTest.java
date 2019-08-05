package com.fr.swift.source.split.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lucifer
 * @date 2019-07-31
 * @description
 * @since advanced swift 1.0
 */
public class JsonSubRowTest {

    @Test
    public void testEmpty() {
        JsonSubRow subRow = new JsonSubRow();
        Assert.assertTrue(subRow.getSubRow().isEmpty());
    }

    @Test
    public void testNormal() {
        Map<String, Object> splitRow = new HashMap<>();
        splitRow.put("sub1", 100);
        splitRow.put("sub2", "test");
        splitRow.put("sub3", "normal");
        JsonSubRow subRow = new JsonSubRow("test", splitRow);
        Map<String, Object> result = subRow.getSubRow();
        Assert.assertEquals(result.size(), 3);
        Assert.assertEquals(result.get("test.sub1"), 100);
        Assert.assertEquals(result.get("test.sub2"), "test");
        Assert.assertEquals(result.get("test.sub3"), "normal");
        Map<String, Object> combineRow = new HashMap<>();
        combineRow.put("combinesub1", 200);
        combineRow.put("combinesub2", "test2");
        combineRow.put("combinesub3", "normal2");
        JsonSubRow combineSubRow = new JsonSubRow("test2", combineRow);
        subRow.combineSplitRow(combineSubRow);
        result = subRow.getSubRow();
        Assert.assertEquals(result.size(), 6);
        Assert.assertEquals(result.get("test2.combinesub1"), 200);
        Assert.assertEquals(result.get("test2.combinesub2"), "test2");
        Assert.assertEquals(result.get("test2.combinesub3"), "normal2");

    }
}