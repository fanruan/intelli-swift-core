package com.fr.swift.jdbc.rpc.invoke;

import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/8/26
 */
public class SyncObject {
    public void waitForResult(int time, CallBackSync sync) {
        int timeAll = 0;
        while (!sync.isDone()) {
            try {
                Thread.sleep(5);
                timeAll += 5;
            } catch (Exception e) {
                throw Exceptions.runtime("", e);
            }
            if (timeAll > time) {
                throw Exceptions.timeout();
            }
        }
    }

    public void notifyResult(CallBackSync sync, RpcResponse rpc) {
        if (sync != null) {
            sync.setResponse(rpc);
        }
    }
}
