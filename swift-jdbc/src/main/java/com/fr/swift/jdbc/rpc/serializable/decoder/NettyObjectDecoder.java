package com.fr.swift.jdbc.rpc.serializable.decoder;


import com.fr.swift.jdbc.JdbcProperty;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.rpc.serializable.clazz.ClassResolver;
import com.fr.swift.jdbc.rpc.serializable.stream.CompactObjectInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author yee
 * @date 2018/8/26
 */
public class NettyObjectDecoder extends ObjectDecoder {

    @Override
    protected ObjectInputStream createObjectInputStream(byte[] bytes) throws IOException {
        ClassResolver classResolver = JdbcProperty.get().getClassResolver();
        return new CompactObjectInputStream(new ByteArrayInputStream(bytes),
                classResolver);
    }

    @Override
    public Object decode(byte[] bytes) throws Exception {
        int length = length(bytes);
        if (length != bytes.length - 4) {
            throw Exceptions.noCodecResponse();
        }
        byte[] data = new byte[length];
        System.arraycopy(bytes, 4, data, 0, length);
        return super.decode(data);
    }

    @Override
    public Object decodeFromChannel(SocketChannel channel) throws Exception {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        channel.read(lengthBuffer);
        byte[] lengthByte = lengthBuffer.array();
        int length = length(lengthByte);
        if (length <= 0) {
            throw Exceptions.noCodecResponse();
        }
        ByteBuffer contentBuffer = ByteBuffer.allocate(length);
        Thread.sleep(100);
        channel.read(contentBuffer);
        contentBuffer.flip();
        return super.decode(contentBuffer.array());
    }

    private int length(byte[] lengthByte) {
        return (lengthByte[0] & 0xFF) << 24
                | (lengthByte[1] & 0xFF) << 16
                | (lengthByte[2] & 0xFF) << 8
                | (lengthByte[3] & 0xFF);
    }
}
