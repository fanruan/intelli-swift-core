package com.fr.swift.jdbc.rpc.serializable.encoder;

import com.fr.swift.jdbc.rpc.serializable.TestBean;
import com.fr.swift.jdbc.rpc.serializable.decoder.ObjectDecoder;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-11
 */
public class ObjectEncoderTest {

    @Test
    public void encode() throws Exception {
        Object bean = new TestBean("xiaoming", 20);
        byte[] data = new ObjectEncoder().encode(bean);
        Object obj = new ObjectDecoder().decode(data);
        assertTrue(obj instanceof TestBean);
        assertEquals(bean, obj);
        ByteBuffer buffer = new ObjectEncoder().encodeBuf(bean);
        obj = new ObjectDecoder().decode(buffer);
        assertTrue(obj instanceof TestBean);
        assertEquals(bean, obj);
    }
}