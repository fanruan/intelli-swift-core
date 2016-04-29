package com.fr.bi.cluster.socket;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public interface SocketService {

    public byte actionOP();

    public void process(byte cmd, ObjectOutputStream oos, Serializable[] params);

}