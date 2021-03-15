package com.fr.swift.cloud.log;

import com.fr.swift.cloud.util.function.Function;

/**
 * @author anchore
 * @date 12/11/2018
 */
public interface SwiftLoggerFactory<T> extends Function<T, SwiftLogger> {
}