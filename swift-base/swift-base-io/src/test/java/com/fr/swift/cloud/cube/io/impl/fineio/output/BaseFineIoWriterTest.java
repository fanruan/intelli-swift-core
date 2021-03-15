package com.fr.swift.cloud.cube.io.impl.fineio.output;

import com.fineio.accessor.file.IWriteFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BaseFineIoWriterTest {

    @Test
    public void release() {
        BaseFineIoWriter fineIoWriter = mock(BaseFineIoWriter.class, CALLS_REAL_METHODS);
        fineIoWriter.writeFile = mock(IWriteFile.class);

        fineIoWriter.release();

        verify(fineIoWriter.writeFile).close();
    }
}