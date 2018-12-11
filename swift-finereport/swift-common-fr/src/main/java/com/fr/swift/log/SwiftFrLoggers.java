package com.fr.swift.log;

/**
 * @author anchore
 * @date 2018/7/4
 */
public class SwiftFrLoggers implements SwiftLoggerFactory<Void> {

    private static final SwiftLogger LOGGER = new SwiftFrLogger();

    @Override
    public SwiftLogger apply(Void p) {
        return LOGGER;
    }
}