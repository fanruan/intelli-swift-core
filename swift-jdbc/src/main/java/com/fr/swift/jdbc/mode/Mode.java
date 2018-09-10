package com.fr.swift.jdbc.mode;

import com.fr.swift.jdbc.emb.EmbJdbcConnector;
import com.fr.swift.jdbc.proxy.JdbcConnector;
import com.fr.swift.jdbc.rpc.nio.RpcNioConnector;

/**
 * @author yee
 * @date 2018/8/29
 */
public enum Mode {
    EMB {
        @Override
        public JdbcConnector createConnector(String address) {
            return new EmbJdbcConnector();
        }
    }, SERVER {
        @Override
        public JdbcConnector createConnector(String address) {
            return new RpcNioConnector(address);
        }
    };


    public abstract JdbcConnector createConnector(String address);
    public static Mode fromKey(String mode) {
        return Mode.valueOf(mode.toUpperCase());
    }
}
