package com.fr.bi.stable.structure.collection.map;


import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.structure.object.TimeAccessObject;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by GUY on 2014/12/9.
 */
public class TimeDeleteHashMap<K, V> {

    private static long M = 1000;
    private static long MINUTE = 1;

    private static long SECOND = 60;

    private Map<K, TimeAccessObject<V>> map = new ConcurrentHashMap<K, TimeAccessObject<V>>();

    public TimeDeleteHashMap(final Traversal<K> release) {
        final Timer timer = new Timer();
        final Date startDate = new Date();
        final long scheduleTime = M * SECOND * MINUTE;
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                long t = System.currentTimeMillis();
                Iterator<Map.Entry<K, TimeAccessObject<V>>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<K, TimeAccessObject<V>> entry = it.next();
                    if (t - entry.getValue().getTime() >= scheduleTime) {
                        try {
                            release.actionPerformed(entry.getKey());
                        } catch (Exception e) {
                                    BILogger.getLogger().error(e.getMessage(), e);
                        }
                        map.remove(entry.getKey());
                    }
                }
            }
        }, startDate, scheduleTime);
    }

    public V get(K key) {
        synchronized (this) {
            if (map.containsKey(key)) {
                TimeAccessObject<V> v = map.get(key);
                if (v != null) {

                    v.updateTime();
                    return v.getValue();

                }
            }
            return null;
        }
    }

    public void updateTime(K k) {
        synchronized (this) {
            TimeAccessObject<V> v = map.get(k);
            if (v != null) {
                v.updateTime();
            }
        }
    }

    public void put(K k, V v) {
        synchronized (this) {
            TimeAccessObject<V> t = new TimeAccessObject<V>(v);
            map.put(k, t);
        }
    }

    public void remove(K k) {
        map.remove(k);
    }

    public void clear() {
        map.clear();
    }
}