package com.fr.swift.log;

import com.fr.swift.util.function.Function;

/**
 * @author anchore
 * @date 12/11/2018
 */
public interface SwiftLoggerFactory<T> extends Function<T, SwiftLogger> {
}