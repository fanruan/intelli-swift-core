package com.fr.swift.jdbc.mode;

import com.fr.swift.jdbc.emb.EmbJdbcConnector;
import com.fr.swift.jdbc.proxy.JdbcConnector;
import com.fr.swift.jdbc.rpc.nio.RpcNioConnector;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum Mode {
    EMB, SERVER;

    public JdbcConnector createConnector(String address) {
        if (this == EMB) {
            return new EmbJdbcConnector();
        } else {
            return new RpcNioConnector(address);
        }
    }
    public static Mode fromKey(String mode) {
        return Mode.valueOf(mode.toUpperCase());
    }
}
