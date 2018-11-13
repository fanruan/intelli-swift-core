package com.fr.swift.util.concurrent;

import com.fr.swift.util.Util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anchore
 * @date 2018/5/5
 */
abstract class BaseThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    BaseThreadFactory(Class<?> c) {
        this(getSimpleName(c));
    }

    BaseThreadFactory(String namePrefix) {
        Util.requireNonNull(namePrefix);

        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        if (r == null) {
            return null;
        }
        return newThread(group, r, String.format("%s-%d", namePrefix, threadNumber.getAndIncrement()), 0);
    }

    static Thread newThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        Thread t = new Thread(group, target, name, stackSize);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    static String getSimpleName(Class<?> c) {
        String name = c.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }
}