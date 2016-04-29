package com.fr.bi.cluster.socket;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class SocketAbstractService implements SocketService {


    protected Map<Byte, SocketAction> actions = new ConcurrentHashMap<Byte, SocketAction>();

    @Override
    public void process(byte cmd, ObjectOutputStream oos, Serializable[] params) {
        SocketAction action = actions.get(cmd);
        if (action != null) {
            action.actionCMD(oos, params);
        }
    }

}