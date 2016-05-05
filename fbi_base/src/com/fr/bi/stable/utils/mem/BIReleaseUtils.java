package com.fr.bi.stable.utils.mem;

import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.fs.control.UserControl;

import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

/**
 * 释放对象，和外部资源
 * Created by Connery on 2015/12/4.
 */
public class BIReleaseUtils {

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
                    Method clean = buffer.getClass().getMethod("cleaner", new Class[0]);
                    clean.setAccessible(true);
                    sun.misc.Cleaner cleaner = (sun.misc.Cleaner) clean.invoke(buffer, new Object[0]);
                    if (cleaner != null) {
                        cleaner.clean();
                        cleaner.clear();
                    }
                } catch (Exception e) {
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                return null;
            }
        });
    }

    public static void releaseAll(Release[] releases) {
        if (releases == null) {
            return;
        }
        for (int i = 0, len = releases.length; i < len; i++) {
            releases[i].releaseResource();
        }
    }


    public static <T extends Release> void releaseUser(long userId, Map<Long, T> userMap) {
        synchronized (userMap) {
            Long key = userId;
            if (BIUserUtils.isAdministrator(userId)) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            T manager = userMap.get(key);
            if (manager != null) {
                manager.releaseResource();
                userMap.remove(key);
            }
        }
    }
}