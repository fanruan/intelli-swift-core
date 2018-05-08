package com.fr.swift.generate;

import com.fr.swift.config.TestConfDb;
import junit.framework.TestCase;

/**
 * Created by pony on 2018/4/27.
 */
public abstract class BaseConfigTest extends TestCase{
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestConfDb.setConfDb();
    }
}
