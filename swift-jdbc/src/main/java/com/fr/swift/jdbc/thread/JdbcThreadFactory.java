package com.fr.swift.jdbc.thread;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yee
 * @date 2018/9/6
 */
public class JdbcThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private String name;

    private JdbcThreadFactory(String name) {
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new Thread(null, r, String.format("%s-%d", name, threadNumber.getAndIncrement()));
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                t.setContextClassLoader(JdbcThreadFactory.class.getClassLoader());
                return null;
            }
        });
        return t;
    }

    public static class Builder {
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public ThreadFactory build() {
            if (null == name) {
                name = "swift-jdbc-thread";
            }
            return new JdbcThreadFactory(name);
        }
    }
}
