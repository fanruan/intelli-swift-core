package com.fr.swift.cloud.cube.io.impl;

import com.fr.swift.cloud.cube.io.output.ByteArrayWriter;
import com.fr.swift.cloud.util.IoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.nio.charset.Charset;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * @author anchore
 * @date 2019/4/8
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({IoUtil.class})
public class BaseStringWriterTest {
    @Mock
    ByteArrayWriter byteArrayWriter;

    @Test
    public void put() {
        new BaseStringWriter(byteArrayWriter).put(1, "1士大夫");

        verify(byteArrayWriter).put(1, "1士大夫".getBytes(Charset.forName("utf8")));
    }

    @Test
    public void release() {
        mockStatic(IoUtil.class);

        new BaseStringWriter(byteArrayWriter).release();

        verifyStatic(IoUtil.class);
        IoUtil.release(byteArrayWriter);
    }
}