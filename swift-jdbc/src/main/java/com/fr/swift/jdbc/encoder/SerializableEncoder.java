package com.fr.swift.jdbc.encoder;

import java.nio.ByteBuffer;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface SerializableEncoder {
    byte[] encode(Object object) throws Exception;

    ByteBuffer encodeBuf(Object object) throws Exception;
}
