package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.ObjectIo;
import com.fr.swift.cube.io.impl.nio.NioConf.IoType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class StringIoTest extends BaseIoTest {

    @Before
    public void setUp() throws Exception {
        pageSize = 3;
    }

    @Test
    public void test() {
        String[] strings = {"1234567890", "0987654321"};

        ObjectIo<String> io = new StringNio(new NioConf(path, IoType.APPEND, pageSize, pageSize, false));
        for (int i = 0; i < strings.length; i++) {
            io.put(i, strings[i]);
        }
        io.release();

        io = new StringNio(new NioConf(path, IoType.APPEND, pageSize, pageSize, false));
        String third = "0987654321";
        io.put(2, third);
        io.release();

        io = new StringNio(new NioConf(path, IoType.READ, pageSize, pageSize, false));
        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals(strings[i], io.get(i));
        }
        Assert.assertEquals(third, io.get(2));
        io.release();
    }
}