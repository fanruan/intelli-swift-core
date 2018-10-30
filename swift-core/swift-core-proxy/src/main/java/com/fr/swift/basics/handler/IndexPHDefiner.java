package com.fr.swift.basics.handler;


import com.fr.swift.basics.ProcessHandler;
import com.fr.swift.basics.annotation.Process;

/**
 * @author yee
 * @date 2018/10/25
 */
public interface IndexPHDefiner {
    @Process(inf = IndexProcessHandler.class)
    interface IndexProcessHandler extends ProcessHandler {
    }

    @Process(inf = StatusProcessHandler.class)
    interface StatusProcessHandler extends ProcessHandler {
    }
}
