package com.fr.swift.jdbc.rpc.serializable.decoder;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface SerializableDecoder {
    /**
     * 从byte数组反序列化
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    Object decode(byte[] bytes) throws Exception;

    /**
     * 从ByteBuffer反序列化
     *
     * @param buf
     * @return
     * @throws Exception
     */
    Object decode(ByteBuffer buf) throws Exception;

    /**
     * 从SocketChannel反序列化
     *
     * @param channel
     * @return
     * @throws Exception
     */
    Object decodeFromChannel(SocketChannel channel) throws Exception;
}
