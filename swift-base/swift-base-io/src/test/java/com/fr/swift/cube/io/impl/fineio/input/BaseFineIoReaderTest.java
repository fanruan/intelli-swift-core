package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.io.Buffer;
import com.fineio.io.file.IOFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BaseFineIoReaderTest {

    @Mock
    IOFile<? extends Buffer> ioFile;
    @InjectMocks
    BaseFineIoReader fineIoReader = new FineIoReader();

    @Test
    public void isReadable() {
        assertFalse(fineIoReader.isReadable());
        fineIoReader.ioFile = ioFile;

        doReturn(true, false).when(ioFile).exists();

        assertTrue(fineIoReader.isReadable());
        assertFalse(fineIoReader.isReadable());
    }

    @Test
    public void release() {
        fineIoReader.release();

        verify(ioFile).close();
    }

    class FineIoReader extends BaseFineIoReader {
    }
}