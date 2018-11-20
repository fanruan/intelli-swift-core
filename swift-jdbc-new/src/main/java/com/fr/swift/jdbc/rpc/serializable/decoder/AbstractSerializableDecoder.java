package com.fr.swift.jdbc.rpc.serializable.decoder;

import java.nio.ByteBuffer;

/**
 * @author yee
 * @date 2018/8/26
 */
public abstract class AbstractSerializableDecoder implements SerializableDecoder {

    @Override
    public Object decode(ByteBuffer buf) throws Exception {
        return decode(buf.array());
    }

}
