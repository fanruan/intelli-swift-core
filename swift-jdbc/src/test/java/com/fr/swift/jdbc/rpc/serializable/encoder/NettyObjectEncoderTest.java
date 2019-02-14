package com.fr.swift.jdbc.rpc.serializable.encoder;

import com.fr.swift.jdbc.rpc.serializable.TestBean;
import com.fr.swift.jdbc.rpc.serializable.decoder.NettyObjectDecoder;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-11
 */
public class NettyObjectEncoderTest {

    @Test
    public void encode() throws Exception {
        Object bean = new TestBean("xiaoming", 20);
        byte[] data = new NettyObjectEncoder().encode(bean);
        Object obj = new NettyObjectDecoder().decode(data);
        assertTrue(obj instanceof TestBean);
        assertEquals(bean, obj);
        ByteBuffer buffer = new NettyObjectEncoder().encodeBuf(bean);
        obj = new NettyObjectDecoder().decode(buffer);
        assertTrue(obj instanceof TestBean);
        assertEquals(bean, obj);
    }
}