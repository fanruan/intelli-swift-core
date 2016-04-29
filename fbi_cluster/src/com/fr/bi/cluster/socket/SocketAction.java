package com.fr.bi.cluster.socket;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface SocketAction {

    public byte getCMD();

    /**
     * 执行操作
     *
     * @param oos    输出
     * @param params 参数
     */
    public void actionCMD(ObjectOutputStream oos, Serializable[] params);


}