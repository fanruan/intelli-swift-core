package com.fr.swift.log.impl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggerFactory;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * @author anchore
 * @date 2019/1/3
 */
public class NoopLoggers implements SwiftLoggerFactory<Void> {

    private static final NoopLogger LOGGER = new NoopLogger();

    public static void main(String[] args) {
        OperatingSystemMXBean mb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}