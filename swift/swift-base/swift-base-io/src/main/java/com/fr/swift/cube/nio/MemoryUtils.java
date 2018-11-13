package com.fr.swift.cube.nio;

import java.nio.MappedByteBuffer;

/**
 * 内存工具
 * Created by Connery on 2015/12/4.
 */
public class MemoryUtils {
    private static final boolean IS_FORCE_NIO_WRITER = false;

    /**
     * 清理
     *
     * @param buffer 缓冲池
     */
    public static void un_map(final MappedByteBuffer buffer) {
        if (buffer != null) {
            if (IS_FORCE_NIO_WRITER) {
                buffer.force();
            }
            ReleaseUtils.doClean(buffer);
        }
    }
}