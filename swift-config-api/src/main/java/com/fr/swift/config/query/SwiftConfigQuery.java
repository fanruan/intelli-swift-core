package com.fr.swift.config.query;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.util.function.Function;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfigQuery<T> extends Function<ConfigSession, T> {
}
