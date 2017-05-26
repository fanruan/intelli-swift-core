package com.fr.bi.stable.utils.mem;

import com.fr.bi.manager.PerformancePlugManager;

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
            if (PerformancePlugManager.getInstance().isForceWriter()){
                buffer.force();
            }
            BIReleaseUtils.doClean(buffer);
        }
    }
}