package com.fr.swift.jdbc.rpc.serializable.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author yee
 * @date 2018/8/26
 */
public class ObjectEncoder extends AbstractSerializableEncoder {
    @Override
    public byte[] encode(Object object) throws Exception {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = createObjectOutputStream(bos);
            oos.writeObject(object);
            return bos.toByteArray();
        } finally {
            if (null != oos) {
                oos.close();
            }
        }
    }

    protected ObjectOutputStream createObjectOutputStream(OutputStream os) throws IOException {
        return new ObjectOutputStream(os);
    }
}
