package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.command.SwiftConfigCommandBus;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigTransaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public abstract class BaseSwiftConfigCommandBus<T> implements SwiftConfigCommandBus<T> {
    protected List<SaveOrUpdateListener<T>> listeners = new ArrayList<>();

    @Override
    public <R> R transaction(SwiftConfigCommand<R> fn) throws SQLException {
        try (ConfigSession session = createSession()) {
            ConfigTransaction tx = session.beginTransaction();
            R apply = fn.apply(session);
            tx.commit();
            return apply;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public void addSaveOrUpdateListener(SaveOrUpdateListener<T> listener) {
        listeners.add(listener);
    }

    /**
     * 创建session
     *
     * @return
     * @throws SQLException
     */
    protected abstract ConfigSession createSession() throws SQLException;
}
