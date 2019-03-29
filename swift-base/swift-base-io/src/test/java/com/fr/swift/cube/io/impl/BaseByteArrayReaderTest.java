package com.fr.swift.cube.io.impl;

import com.fr.swift.cube.io.input.ByteReader;
import com.fr.swift.cube.io.input.IntReader;
import com.fr.swift.cube.io.input.LongReader;
import com.fr.swift.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({IoUtil.class})
public class BaseByteArrayReaderTest {

    @Mock
    ByteReader dataReader;
    @Mock
    LongReader posReader;
    @Mock
    IntReader lenReader;

    @Test
    public void isReadable() {
        assertFalse(new BaseByteArrayReader(null, posReader, lenReader).isReadable());
        assertFalse(new BaseByteArrayReader(dataReader, null, lenReader).isReadable());
        assertFalse(new BaseByteArrayReader(dataReader, posReader, null).isReadable());

        assertFalse(new BaseByteArrayReader(dataReader, posReader, lenReader).isReadable());

        when(dataReader.isReadable()).thenReturn(true);
        assertFalse(new BaseByteArrayReader(dataReader, posReader, lenReader).isReadable());

        when(posReader.isReadable()).thenReturn(true);
        assertFalse(new BaseByteArrayReader(dataReader, posReader, lenReader).isReadable());

        when(lenReader.isReadable()).thenReturn(true);
        assertTrue(new BaseByteArrayReader(dataReader, posReader, lenReader).isReadable());
    }

    @Test
    public void get() {
        when(posReader.get(0)).thenReturn(10L);
        when(lenReader.get(0)).thenReturn(3);
        when(dataReader.get(10)).thenReturn((byte) 1);
        when(dataReader.get(11)).thenReturn((byte) 2);
        when(dataReader.get(12)).thenReturn((byte) 3);

        assertArrayEquals(new byte[]{1, 2, 3}, new BaseByteArrayReader(dataReader, posReader, lenReader).get(0));
    }

    @Test
    public void release() {
        mockStatic(IoUtil.class);

        new BaseByteArrayReader(dataReader, posReader, lenReader).release();

        verifyStatic(IoUtil.class);
        IoUtil.release(dataReader, lenReader, posReader);
    }
}