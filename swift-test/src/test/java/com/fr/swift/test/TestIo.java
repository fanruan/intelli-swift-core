package com.fr.swift.test;

import org.junit.BeforeClass;

/**
 * @author anchore
 * @date 2018/5/8
 */
public abstract class TestIo {
    @BeforeClass
    public static void beforeClass() {
        Preparer.preparePath();
    }
}