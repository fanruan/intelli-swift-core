package com.fr.bi.cluster.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connery on 2015/4/9.
 */
public class SessionMapContainer {
    private static SessionMapContainer instance = new SessionMapContainer();
    Map<String, String> slaverMasterSessionMapper;

    private SessionMapContainer() {
        slaverMasterSessionMapper = new ConcurrentHashMap<String, String>();
    }

    public static SessionMapContainer getInstance() {
        return instance;
    }

    public void addSessionMapper(String slaverSession, String masterSession) {
        slaverMasterSessionMapper.put(slaverSession, masterSession);
    }

    public String getSession(String slaverSession) {
        if (slaverSession != null && slaverMasterSessionMapper.containsKey(slaverSession)) {
            return slaverMasterSessionMapper.get(slaverSession);
        } else {
            return "";
        }
    }

    public void logoutSlaverSession(String slaverSession) {
        if (slaverMasterSessionMapper.containsKey(slaverSession)) {
            slaverMasterSessionMapper.remove(slaverSession);
        }
    }

    public void clear() {
        slaverMasterSessionMapper.clear();
    }

}