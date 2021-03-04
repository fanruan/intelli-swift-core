package com.fr.swift.cloud.cube.io.impl.fineio.input;

import com.fineio.accessor.file.IReadFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BaseFineIoReaderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    IReadFile<?> readFile;
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private BaseFineIoReader fineIoReader;

    @Test
    public void isReadable() throws Exception {
        assertFalse(fineIoReader.isReadable());
        fineIoReader.readFile = readFile;

        when(readFile.exists()).thenReturn(true, false);

        assertTrue(fineIoReader.isReadable());
        assertFalse(fineIoReader.isReadable());
    }

    @Test
    public void release() {
        fineIoReader.readFile = readFile;

        fineIoReader.release();

        verify(readFile).close();
    }
}