package com.fr.swift.io;

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
        SameObjectIo<String> io = new StringNio(path, pageSize);
        String[] strings = {UUID.randomUUID().toString(), UUID.randomUUID().toString()};
        for (int i = 0; i < strings.length; i++) {
            io.put(i, strings[i]);
        }
        io.release();

        io = new StringNio(path, pageSize);
        for (int i = 0; i < strings.length; i++) {
            Assert.assertEquals(strings[i], io.get(i));
        }
        io.release();
    }
}