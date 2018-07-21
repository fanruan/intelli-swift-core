package com.fr.swift.io.nio;

import com.fr.swift.io.Io;

/**
 * @author anchore
 * @date 2018/7/20
 */
abstract class BaseNio implements Io {
    String basePath;

    boolean write = true;

    BaseNio(String basePath) {
        this.basePath = basePath;
    }
}