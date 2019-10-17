package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.input.ByteArrayReader;
import com.fr.swift.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/4/8
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({IoUtil.class})
public class BaseStringReaderTest {
    @Mock
    ByteArrayReader byteArrayReader;

    @Test
    public void isReadable() {
        assertFalse(new BaseStringReader(null).isReadable());

        when(byteArrayReader.isReadable()).thenReturn(true);

        assertTrue(new BaseStringReader(byteArrayReader).isReadable());
    }

    @Test
    public void get() {
        when(byteArrayReader.get(0)).thenReturn("1士大夫".getBytes(Charset.forName("utf8")));

        assertEquals("1士大夫", new BaseStringReader(byteArrayReader).get(0));
    }

    @Test
    public void release() {
        mockStatic(IoUtil.class);

        new BaseStringReader(byteArrayReader).release();

        verifyStatic(IoUtil.class);
        IoUtil.release(byteArrayReader);
    }
}