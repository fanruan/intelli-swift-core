package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.ByteIo;
import com.fr.swift.cube.io.impl.nio.NioConf.IoType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class ByteIoTest extends BaseIoTest {

    @Before
    public void setUp() throws Exception {
        pageSize = 2;
    }

    @Test
    public void test() {
        ByteIo io = new ByteNio(new NioConf(path, IoType.OVERWRITE, pageSize, pageSize, false));
        for (int i = 0; i < data.length; i++) {
            io.put(i, data[i]);
        }
        io.release();
        io = new ByteNio(new NioConf(path, IoType.READ, pageSize, pageSize, false));
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals(data[i], io.get(i));
        }
        io.release();

        io = new ByteNio(new NioConf(path, IoType.OVERWRITE, pageSize, pageSize, false));
        io.put(2, (byte) 5);
        io.put(9, (byte) 4);
        io.release();
        io = new ByteNio(new NioConf(path, IoType.OVERWRITE, pageSize, pageSize, false));
        for (int i = 0; i < data.length; i++) {
            if (i == 2) {
                Assert.assertEquals(5, io.get(i));
                continue;
            }
            if (i == 9) {
                Assert.assertEquals(4, io.get(i));
                continue;
            }
            Assert.assertEquals(data[i], io.get(i));
        }
        io.release();
    }
}