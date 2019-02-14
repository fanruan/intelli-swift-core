package com.fr.swift.jdbc.rpc.serializable.encoder;

import java.nio.ByteBuffer;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface SerializableEncoder {
    /**
     * 序列化对象到数组
     *
     * @param object
     * @return
     * @throws Exception
     */
    byte[] encode(Object object) throws Exception;

    /**
     * 序列化对象到ByteBuffer
     * @param object
     * @return
     * @throws Exception
     */
    ByteBuffer encodeBuf(Object object) throws Exception;
}
