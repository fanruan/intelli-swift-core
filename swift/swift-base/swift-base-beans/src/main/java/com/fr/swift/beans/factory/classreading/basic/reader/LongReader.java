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
public class LongReader {

    /**
     * 4个字节read
     *
     * @param inputStream
     * @return
     */
    public static long read(InputStream inputStream) {
        byte[] bytes = new byte[4];
        try {
            inputStream.read(bytes);
        } catch (IOException e) {
            SwiftLoggers.getLogger().error(e);
        }
        long num = 0;
        for (int i = 0; i < bytes.length; i++) {
            num <<= 8;
            num |= (bytes[i] & 0xff);
        }
        return num;
    }
}
