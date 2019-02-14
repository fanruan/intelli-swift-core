package com.fr.swift.log;

import com.fr.log.FineLoggerFactory;
import com.fr.swift.log.impl.BaseSwiftLogger;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.Optional;

/**
 * @author anchore
 * @date 2018/7/4
 */
class SwiftFrLogger extends BaseSwiftLogger implements SwiftLogger {

    SwiftFrLogger() {
    }

    @Override
    public void debug(String s, Object... objects) {
        Pair<String, Optional<Throwable>> pair = format(s, objects);
        if (pair.getValue().isPresent()) {
            FineLoggerFactory.getLogger().debug(pair.getKey(), pair.getValue().get());
        } else {
            FineLoggerFactory.getLogger().debug(pair.getKey());
        }
    }

    @Override
    public void info(String s, Object... objects) {
        Pair<String, Optional<Throwable>> pair = format(s, objects);
        if (pair.getValue().isPresent()) {
            FineLoggerFactory.getLogger().info(pair.getKey(), pair.getValue().get());
        } else {
            FineLoggerFactory.getLogger().info(pair.getKey());
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        Pair<String, Optional<Throwable>> pair = format(s, objects);
        if (pair.getValue().isPresent()) {
            FineLoggerFactory.getLogger().warn(pair.getKey(), pair.getValue().get());
        } else {
            FineLoggerFactory.getLogger().warn(pair.getKey());
        }
    }

    @Override
    public void error(String s, Object... objects) {
        Pair<String, Optional<Throwable>> pair = format(s, objects);
        if (pair.getValue().isPresent()) {
            FineLoggerFactory.getLogger().error(pair.getKey(), pair.getValue().get());
        } else {
            FineLoggerFactory.getLogger().error(pair.getKey());
        }
    }
}