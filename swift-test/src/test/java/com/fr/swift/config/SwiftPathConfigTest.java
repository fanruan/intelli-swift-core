package com.fr.swift.config;

import com.fr.annotation.Test;
import com.fr.swift.generate.BaseTest;

/**
 * This class created on 2018/5/7
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class SwiftPathConfigTest extends BaseTest {

    @Test
    public void testSetAndGet() {
        String path = String.valueOf(System.currentTimeMillis());
        SwiftPathConfig.getInstance().setPath(path);
        assertEquals(SwiftPathConfig.getInstance().getPath(), path);
    }
}
