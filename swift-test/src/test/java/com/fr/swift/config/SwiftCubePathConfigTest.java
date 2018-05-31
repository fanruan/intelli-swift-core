package com.fr.swift.config;

import com.fr.annotation.Test;
import com.fr.swift.generate.BaseTest;
import com.fr.swift.test.Preparer;

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
        Preparer.preparePath();
    }

    @Test
    public void testSetAndGet() {
        String path = String.valueOf(System.currentTimeMillis());
        String newPath = System.getProperty("user.dir") + "/" + path;
        SwiftCubePathConfig.getInstance().setPath(newPath);
        assertEquals(SwiftCubePathConfig.getInstance().getPath(), newPath);
    }
}
