package com.fr.swift.query.session;

import com.fr.swift.util.Clearable;

/**
 * @author yee
 * @date 2018/6/19
 */
public interface SessionFactory extends Clearable {
    Session openSession(SessionBuilder sessionBuilder);

    void setCacheTimeout(long cacheTimeout);

    void setSessionTimeout(long sessionTimeout);

    Session openSession(SessionBuilder sessionBuilder, String queryId);

    void setMaxIdle(int maxIdle);
}
