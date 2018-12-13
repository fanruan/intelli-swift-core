package com.fr.swift.base.json;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2018-12-04
 */
public class JsonBuilderTest {
    private static final String JSON = "{\"testInf\":{\"name\":\"testName\",\"type\":\"B\",\"age\":100},\"type\":\"C\"}";

    @Test
    public void writeJsonString() throws Exception {
        TestB b = new TestB();
        b.setAge(100);
        b.setName("testName");
        TestInf inf = new TestC(b);
        assertEquals(JSON, JsonBuilder.writeJsonString(inf));
    }

    @Test
    public void readValue() throws Exception {
        TestC inf = (TestC) JsonBuilder.readValue(JSON, TestInf.class);
        TestB b = (TestB) inf.getTestInf();
        assertEquals(b.getName(), "testName");
        assertEquals(b.getAge(), 100);
    }
}