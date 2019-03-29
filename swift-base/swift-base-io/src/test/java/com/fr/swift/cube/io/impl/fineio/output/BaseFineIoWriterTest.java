package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.io.Buffer;
import com.fineio.io.file.IOFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Mockito.verify;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BaseFineIoWriterTest {

    @Mock
    IOFile<? extends Buffer> ioFile;
    @InjectMocks
    BaseFineIoWriter fineIoWriter = new FineIoWriter();

    @Test
    public void release() {
        fineIoWriter.release();

        verify(ioFile).close();
    }

    class FineIoWriter extends BaseFineIoWriter {
    }
}