package com.fr.bi.cal.stable.engine;


import com.fr.bi.stable.utils.BIUserUtils;
import com.fr.fs.control.UserControl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实时报表的路径生成器
 * Created by GUY on 2014/12/12.
 */
public final class TempPathGenerator {
    private final static Map<TempCubeTask, String> TEMP_PATH = new ConcurrentHashMap<TempCubeTask, String>();
    /**
     * 一个线程只能用一个path
     */
    private final static Map<Long, String> THREAD_PATH = new HashMap<Long, String>();

    public static String getTempPath(TempCubeTask task) {
        synchronized (TEMP_PATH) {
            Long key = task.getUserId();
            boolean useAdministrator = BIUserUtils.isAdministrator(task.getUserId());
            if (useAdministrator) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            task.setUserId(key);
            return TEMP_PATH.get(task);
        }
    }

    public static String createTempPath() {
        return UUID.randomUUID().toString();
    }

    public static void removeTempPath(long threadId) {
        THREAD_PATH.remove(threadId);
    }

    public static void setTempPath(TempCubeTask task, String newPath) {
        synchronized (TEMP_PATH) {
            Long key = task.getUserId();
            boolean useAdministrator = BIUserUtils.isAdministrator(task.getUserId());
            if (useAdministrator) {
                key = UserControl.getInstance().getSuperManagerID();
            }
            task.setUserId(key);
            String path = TEMP_PATH.get(task);
            if (path != null) {
                TEMP_PATH.remove(task);
            }
            TEMP_PATH.put(task, newPath);
        }
    }

    public static void release(TempCubeTask task) {
        if (task != null && TEMP_PATH.containsKey(task)) {
            TEMP_PATH.remove(task);
        }
    }
}