package com.fr.swift.beans.factory.classreading;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class created on 2018/12/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class TestInputStream extends InputStream {

    private byte[] bytes;

    private int index = 0;

    public TestInputStream(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public int read(byte b[]) throws IOException {
        System.arraycopy(bytes, index, b, 0, b.length);
        index += b.length;
        return b.length;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
