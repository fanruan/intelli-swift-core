package com.fr.swift.io;

import com.fr.swift.io.nio.NioConf;
import com.fr.swift.io.nio.NioConf.IoType;
import com.fr.swift.io.nio.StringNio;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * @author anchore
 * @date 2018/7/21
 */
public class StringIoTest extends BaseIoTest {

    @Before
    public void setUp() throws Exception {
        pageSize = 5;
    }

    @Test
    public void test() {
        String[] strings = {UUID.randomUUID().toString(), UUID.randomUUID().toString()};

        ObjectIo<String> io = new StringNio(new NioConf(path, IoType.APPEND, pageSize, false));
        for (int i = 0; i < strings.length; i++) {
            io.put(i, strings[i]);
        }
        io.release();

        io = new StringNio(new NioConf(path, IoType.APPEND, pageSize, false));
        String third = UUID.randomUUID().toString();
        io.put(2, third);
        io.release();

        io = new StringNio(new NioConf(path, IoType.READ, pageSize, false));
        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals(strings[i], io.get(i));
        }
        Assert.assertEquals(third, io.get(2));
        io.release();
    }
}