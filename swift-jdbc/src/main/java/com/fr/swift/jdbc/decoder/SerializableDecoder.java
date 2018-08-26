package com.fr.swift.jdbc.decoder;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface SerializableDecoder {
    Object decode(byte[] bytes) throws Exception;

    Object decode(ByteBuffer buf) throws Exception;

    Object decodeFromChannel(SocketChannel channel) throws Exception;
}
