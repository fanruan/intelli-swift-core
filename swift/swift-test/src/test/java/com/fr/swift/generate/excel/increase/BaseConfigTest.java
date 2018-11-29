package com.fr.swift.generate;

import com.fr.swift.test.Preparer;
import org.junit.Before;

/**
 * Created by pony on 2018/4/27.
 */
public abstract class BaseConfigTest {
    @Before
    public void setUp() throws Exception {
        Preparer.prepareFrEnv();
        Preparer.prepareContext();
        Preparer.prepareConfDb();
    }
}