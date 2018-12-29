package com.fr.swift.jdbc.rpc.serializable.encoder;

import java.nio.ByteBuffer;

/**
 * @author yee
 * @date 2018/8/26
 */
public abstract class AbstractSerializableEncoder implements SerializableEncoder {

    @Override
    public ByteBuffer encodeBuf(Object object) throws Exception {
        return ByteBuffer.wrap(encode(object));
    }
}
