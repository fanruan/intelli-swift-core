package com.fr.swift.config.command.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.condition.SwiftConfigCondition;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.log.SwiftLoggers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yee
 * @date 2019-07-30
 */
public class SwiftHibernateConfigCommandBus<T> extends BaseSwiftConfigCommandBus<T> {
    protected Class<? extends T> tClass;

    public SwiftHibernateConfigCommandBus(Class<? extends T> tClass) {
        this.tClass = tClass;
    }

    @Override
    public T save(final T obj) throws SQLException {
        return transaction(SwiftConfigCommands.ofSave(obj));
    }

    @Override
    public List<T> save(final Collection<T> objs) throws SQLException {
        return transaction(new SwiftConfigCommand<List<T>>() {
            @Override
            public List<T> apply(ConfigSession p) {
                List<T> apply = new ArrayList<>();
                for (T obj : objs) {
                    p.save(obj);
                    apply.add(obj);
                }
                return Collections.unmodifiableList(apply);
            }
        });
    }

    @Override
    public T merge(final T obj) {
        try {
            return transaction(SwiftConfigCommands.ofMerge(obj));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("merge object {} failed", obj, e);
            return null;
        }
    }

    @Override
    public List<T> merge(final Collection<T> objs) {
        try {
            return transaction(new SwiftConfigCommand<List<T>>() {
                @Override
                public List<T> apply(ConfigSession p) {
                    List<T> apply = new ArrayList<>();
                    for (T obj : objs) {
                        apply.add((T) p.merge(obj));
                    }
                    return Collections.unmodifiableList(apply);
                }
            });
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("merge object {} failed", objs, e);
            return Collections.emptyList();
        }
    }

    @Override
    public int delete(SwiftConfigCondition condition) {
        try {
            return transaction(SwiftConfigCommands.ofDelete(tClass, condition));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("delete object failed", e);
            return 0;
        }
    }

    @Override
    public int deleteCascade(SwiftConfigCondition condition) {
        try {
            return transaction(SwiftConfigCommands.ofDeleteCascade(tClass, condition));
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error("delete object failed", e);
            return 0;
        }
    }

    @Override
    protected ConfigSession createSession() throws SQLException {
        try {
            return SwiftContext.get().getBean(ConfigSessionCreator.class).createSession();
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }
}
