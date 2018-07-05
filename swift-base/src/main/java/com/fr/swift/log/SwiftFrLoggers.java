package com.fr.swift.log;

import com.fr.log.FineLoggerFactory;
import com.fr.swift.util.function.Function;

/**
 * @author anchore
 * @date 2018/7/4
 */
public class SwiftFrLoggers implements Function<Void, SwiftLogger> {
    @Override
    public SwiftLogger apply(Void p) {
        return new SwiftFrLogger(FineLoggerFactory.getLogger());
    }
}