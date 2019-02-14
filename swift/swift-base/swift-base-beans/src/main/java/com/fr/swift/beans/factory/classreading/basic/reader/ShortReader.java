package com.fr.swift.beans.factory.classreading.basic.reader;

import com.fr.swift.log.SwiftLoggers;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class created on 2018/12/3
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ShortReader {

    /**
     * 1个字节read
     *
     * @param inputStream
     * @return
     */
    public static short read(InputStream inputStream) {
        byte[] bytes = new byte[1];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
        short value = (short) (bytes[0] & 0xFF);
        return value;
    }
}
