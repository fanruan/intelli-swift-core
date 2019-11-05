package com.fr.swift.log.impl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggerFactory;

/**
 * @author anchore
 * @date 2019/1/8
 */
public class SystemLoggers implements SwiftLoggerFactory<Void> {

    private static final SystemLogger LOGGER = new SystemLogger();

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}