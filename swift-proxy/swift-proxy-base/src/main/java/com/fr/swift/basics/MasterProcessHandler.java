package com.fr.swift.basics;

import com.fr.swift.basics.annotation.Process;

/**
 * @author yee
 * @date 2018/10/24
 */
@Process(inf = MasterProcessHandler.class)
public interface MasterProcessHandler extends ProcessHandler {
    URL processMasterURL();
}
