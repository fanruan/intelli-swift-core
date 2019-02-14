package com.fr.swift.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/8/17
 */
public class StringsTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void unifySlash() {
        String a = "/a//b/\\c\\/d\\\\e/f 1\\",
                b = "/a//b/c",
                c = "a\\b\\\\c\\\\\\";

        assertEquals("/a/b/c/d/e/f 1", Strings.unifySlash(a));
        assertEquals("/a/b/c", Strings.unifySlash(b));
        assertEquals("a/b/c", Strings.unifySlash(c));
    }
}