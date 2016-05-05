package com.finebi.cube;

import com.fr.bi.common.inter.Release;
import com.fr.bi.stable.io.newio.NIOHelper;
import com.fr.bi.stable.io.newio.NIOReader;
import com.fr.bi.stable.utils.code.BILogger;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SingleUserNIOReadManager implements Release {

    private Map<String, NIOReader<?>> NIOMap = new ConcurrentHashMap<String, NIOReader<?>>();
    private long userId;
    private NIOHelper helper;
    private static Map<Long, SingleUserNIOReadManager> READINGMAP = new ConcurrentHashMap<Long, SingleUserNIOReadManager>();

    public SingleUserNIOReadManager(long userId) {
        this.userId = userId;
    }

    public SingleUserNIOReadManager(long userId, NIOHelper helper) {
        this(userId);
        this.helper = helper;
    }

    @Override
    public void releaseResource() {
        synchronized (this) {
            for (Entry<String, NIOReader<?>> o : NIOMap.entrySet()) {
                if (o.getValue() != null) {
                    try {
                        o.getValue().releaseResource();
                    } catch (Throwable e) {
                        BILogger.getLogger().error(e.getMessage(), e);
                    }
                }
            }
            NIOMap.clear();
        }
    }

    public <T extends NIOReader<?>> T getNIOReader(String key,
                                                   Class<T> c) {
        return getNIOReader(key, c, new File(key));
    }

    public <T extends NIOReader<?>> T getNIOReader(String key,
                                                   Class<T> c, File f) {
        if (key == null) {
            return null;
        }
        if (NIOMap.containsKey(key)) {
            return (T) NIOMap.get(key);
        }
        synchronized (this) {
            if (NIOMap.containsKey(key)) {
                return (T) NIOMap.get(key);
            }
            if (c == null || f == null || (!f.exists())) {
                return null;
            }
            if (helper != null && (!helper.canCreateNewNIOObject())) {
                return null;
            }
            T result = null;
            try {
                result = c.getConstructor(f.getClass()).newInstance(f);
            } catch (Exception e) {
                BILogger.getLogger().error(e.getMessage(), e);
            }
            if (result == null) {
                return null;
            }
            NIOMap.put(key, result);
            return result;
        }
    }

    public long getUserId() {
        return userId;
    }

}