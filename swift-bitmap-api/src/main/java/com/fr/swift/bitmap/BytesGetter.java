package com.fr.swift.bitmap;

import java.io.OutputStream;

/**
 * @author anchore
 */
public interface BytesGetter {
    /**
     * 转成byte数组
     *
     * @return byte数组
     */
    byte[] toBytes();

    void writeBytes(OutputStream output);
}
