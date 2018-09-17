package com.fr.swift.cube.io.impl.fineio.connector;

import com.fineio.storage.Connector;
import com.fr.stable.fun.mark.Immutable;

/**
 * FineIO Connector插件生成Connector接口
 *
 * @author yee
 * @date 2017/8/4
 */
public interface ConnectorProcessor extends Immutable {
    int CURRENT_LEVEL = 1;
    String MARK_STRING = "ConnectorProcessor";

    Connector createConnector();
}
