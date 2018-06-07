package com.fr.swift.generate;

import com.fr.swift.config.TestConfDb;
import org.junit.Before;

/**
 * Created by pony on 2018/4/27.
 */
public abstract class BaseConfigTest {
    @Before
    public void setUp() throws Exception {
        TestConfDb.setConfDb();
    }
}
