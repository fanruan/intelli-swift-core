package com.fr.swift.basics.handler;

import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.Process;

/**
 * @author yee
 * @date 2018/10/25
 */
@Process(inf = QueryableProcessHandler.class)
public interface QueryableProcessHandler extends ProcessHandler {
}
