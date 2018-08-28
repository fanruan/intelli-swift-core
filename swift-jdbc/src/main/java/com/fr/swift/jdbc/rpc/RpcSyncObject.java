package com.fr.swift.jdbc.rpc;

import com.fr.swift.jdbc.exception.RpcException;
import com.fr.swift.rpc.bean.RpcResponse;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcSyncObject {
    public void waitForResult(int time, RpcCallBackSync sync) {
        int timeAll = 0;
        while (!sync.isDone()) {
            try {
                Thread.sleep(5);
                timeAll += 5;
            } catch (Exception e) {
                throw new RpcException(e);
            }
            if (timeAll > time) {
                throw new RpcException("request time out");
            }
        }
    }

    public void notifyResult(RpcCallBackSync sync, RpcResponse rpc) {
        if (sync != null) {
            sync.setResponse(rpc);
        }
    }
}
