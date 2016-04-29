package com.fr.bi.stable.utils.mem;

import java.nio.MappedByteBuffer;

/**
 * 内存工具
 * Created by Connery on 2015/12/4.
 */
public class BIMemoryUtils {


    /**
     * 清理
     *
     * @param buffer 缓冲池
     */
    public static void un_map(final MappedByteBuffer buffer) {
        if (buffer != null) {
            buffer.force();
            BIReleaseUtils.doClean(buffer);
        }
    }
}