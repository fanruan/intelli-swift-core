package com.fr.swift.cube.nio;

import com.fr.swift.log.SwiftLoggers;

import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * 释放对象，和外部资源
 * Created by Connery on 2015/12/4.
 */
public class ReleaseUtils {

    /**
     * 清理
     *
     * @param buffer 缓冲池
     */
    public static void doClean(final MappedByteBuffer buffer) {
        AccessController.doPrivileged(new PrivilegedAction() {
            @Override
            public Object run() {
                try {
                    Method clean = buffer.getClass().getMethod("cleaner");
                    clean.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) clean.invoke(buffer, new Object[0]);
                    if (cleaner != null) {
                        cleaner.clean();
                        cleaner.clear();
                    }
                } catch (Exception e) {
                    SwiftLoggers.getLogger().error(e.getMessage(), e);
                }
                return null;
            }
        });
    }
}