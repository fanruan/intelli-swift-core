package com.fr.swift.jdbc.sql;

import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.jdbc.rpc.connection.EmbJdbcConnector;
import com.fr.swift.jdbc.rpc.invoke.SimpleExecutor;

import java.util.Properties;

/**
 * @author yee
 * @date 2018/11/16
 */
public class EmbSwiftConnection extends BaseSwiftConnection {
    EmbSwiftConnection(UnregisteredDriver driver, Properties properties) {
        super(driver, properties);
    }

    @Override
    protected JdbcExecutor createJdbcExecutor(String host, int port) {
        return new SimpleExecutor(new EmbJdbcConnector(), connectionTimeout());
    }

    @Override
    protected JdbcExecutor createJdbcExecutor(String address) {
        return new SimpleExecutor(new EmbJdbcConnector(), connectionTimeout());
    }
}
