package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.io.Buffer;
import com.fineio.io.file.FileBlock;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/27
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
public class BaseFineIoReaderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    IOFile<? extends Buffer> ioFile;

    private BaseFineIoReader fineIoReader = new FineIoReader();

    @Test
    public void isReadable() throws Exception {
        assertFalse(fineIoReader.isReadable());
        fineIoReader.ioFile = ioFile;

        Connector connector = mock(Connector.class);
        Whitebox.setInternalState(ioFile, "connector", connector);

        when(connector.exists(Whitebox.<FileBlock>invokeMethod(ioFile, "createHeadBlock"))).thenReturn(true);
        when(connector.exists(Whitebox.<FileBlock>invokeMethod(ioFile, "createIndexBlock", 0))).thenReturn(true, false);

        assertTrue(fineIoReader.isReadable());
        assertFalse(fineIoReader.isReadable());
    }

    @Test
    public void release() {
        Whitebox.setInternalState(ioFile, "close", true);
        fineIoReader.ioFile = ioFile;

        fineIoReader.release();

        verify(ioFile).close();
    }

    class FineIoReader extends BaseFineIoReader {
    }
}