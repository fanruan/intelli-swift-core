package com.fr.swift.log.impl;

import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggerFactory;

/**
 * @author anchore
 * @date 2019/1/3
 */
public class NoopLoggers implements SwiftLoggerFactory<Void> {

    private static final NoopLogger LOGGER = new NoopLogger();

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}