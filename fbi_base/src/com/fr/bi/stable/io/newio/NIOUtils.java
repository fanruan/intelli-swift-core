package com.fr.bi.stable.io.newio;

import com.fr.bi.stable.utils.mem.BIReleaseUtils;
import com.fr.bi.stable.utils.program.BIConstructorUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NIOUtils {

    public static Map<Long, SingleUserNIOReadManager> READINGMAP = new ConcurrentHashMap<Long, SingleUserNIOReadManager>();

    public static Map<Long, SingleUserNIOReadManager> GENERATINGMAP = new ConcurrentHashMap<Long, SingleUserNIOReadManager>();


    public static SingleUserNIOReadManager getReadingManager(long userId) {
        return BIConstructorUtils.constructObject(userId, SingleUserNIOReadManager.class, READINGMAP);
    }


    public static SingleUserNIOReadManager getGeneratingManager(long userId) {
        return BIConstructorUtils.constructObject(userId, SingleUserNIOReadManager.class, GENERATINGMAP);
    }


    /**
     * for hihidata only
     *
     * @param userId
     */
    public static void releaseUser(long userId) {
        BIReleaseUtils.releaseUser(userId, READINGMAP);
        BIReleaseUtils.releaseUser(userId, GENERATINGMAP);
    }


}