package com.fr.swift.cloud.rpc.serialize;

import java.io.IOException;

/**
 * @author Heng.J
 * @date 2021/7/30
 * @description
 * @since swift-1.2.0
 */
public interface SerializationEncoder extends SerializationCodec {

    byte[] encode(Object msg) throws IOException;
}
