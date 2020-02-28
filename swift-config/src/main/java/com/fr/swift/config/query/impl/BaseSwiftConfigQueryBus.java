package com.fr.swift.config.query.impl;

import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.query.SwiftConfigQuery;
import com.fr.swift.config.query.SwiftConfigQueryBus;
import com.fr.swift.log.SwiftLoggers;

import java.sql.SQLException;

/**
 * @author yee
 * @date 2019-07-30
 */
public abstract class BaseSwiftConfigQueryBus<T> implements SwiftConfigQueryBus<T> {

    @Override
    public <R> R get(SwiftConfigQuery<R> fn) {
        try (ConfigSession session = createSession()) {
            return fn.apply(session);
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("Get swift config failed ", e);
            return null;
        }
    }

    /**
     * 创建session
     *
     * @return
     * @throws SQLException
     */
    protected abstract ConfigSession createSession() throws SQLException;
}
