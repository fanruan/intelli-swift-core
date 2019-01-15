package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.test.TestResource;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class BaseIoTest {
    final byte[] data = new byte[]{-1, 3, 4, 2, 6, 8, 7, 9, 0, 5};

    final String path = TestResource.getRunPath(getClass());

    int pageSize;
}