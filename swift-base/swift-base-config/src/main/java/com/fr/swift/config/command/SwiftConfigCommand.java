package com.fr.swift.config.command;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.util.function.Function;

/**
 * @author yee
 * @date 2019-07-30
 */
public interface SwiftConfigCommand<T> extends Function<ConfigSession, T> {
}
