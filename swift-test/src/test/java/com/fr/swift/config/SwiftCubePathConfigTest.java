package com.fr.swift.config;

import com.fr.swift.generate.BaseTest;
import com.fr.swift.test.Preparer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class created on 2018/5/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftCubePathConfigTest extends BaseTest {

    @Override
    public void setUp() throws Exception {
        Preparer.prepareFrEnv();
    }

    @Test
    public void testSetAndGet() {
        String path = String.valueOf(System.currentTimeMillis());
        String newPath = System.getProperty("user.dir") + "/" + path;
        SwiftCubePathConfig.getInstance().setPath(newPath);
        assertEquals(SwiftCubePathConfig.getInstance().getPath(), newPath);
    }
}
