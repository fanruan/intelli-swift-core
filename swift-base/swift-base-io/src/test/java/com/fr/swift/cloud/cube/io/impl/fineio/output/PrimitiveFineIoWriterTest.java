package com.fr.swift.cloud.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.IOAccessor;
import com.fineio.accessor.Model;
import com.fineio.accessor.buffer.ByteBuf;
import com.fineio.accessor.buffer.DoubleBuf;
import com.fineio.accessor.buffer.IntBuf;
import com.fineio.accessor.buffer.LongBuf;
import com.fineio.accessor.file.IAppendFile;
import com.fineio.accessor.file.IWriteFile;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cloud.cube.io.impl.fineio.connector.ConnectorManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectorManager.class, FineIO.class, IOFile.class})
public class PrimitiveFineIoWriterTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");

    private final IWriteFile<?> ioFile = mock(IWriteFile.class);

    @Before
    public void setUp() throws Exception {
        mockStatic(ConnectorManager.class);
        ConnectorManager connectorManager = mock(ConnectorManager.class);
        when(ConnectorManager.getInstance()).thenReturn(connectorManager);

        Connector connector = mock(Connector.class);
        when(connectorManager.getConnector()).thenReturn(connector);

        IOAccessor ioAccessor = mock(IOAccessor.class);
        setInternalState(FineIOAccessor.INSTANCE, "accessor", ioAccessor);
        when(ioAccessor.createFile(any(Connector.class), any(URI.class), any(Model.class))).thenReturn(ioFile);

        when(ioFile.exists()).thenReturn(true);
    }

    @Test
    public void release() {
        ByteFineIoWriter.build(location, true).release();
        verify(ioFile, atLeastOnce()).close();

        IntFineIoWriter.build(location, true).release();
        verify(ioFile, atLeastOnce()).close();

        LongFineIoWriter.build(location, true).release();
        verify(ioFile, atLeastOnce()).close();

        DoubleFineIoWriter.build(location, true).release();
        verify(ioFile, atLeastOnce()).close();
    }

    @Test
    public void build() {
        ByteFineIoWriter.build(location, true);
        IntFineIoWriter.build(location, true);
        LongFineIoWriter.build(location, true);
        DoubleFineIoWriter.build(location, true);

        when(Whitebox.<IOAccessor>getInternalState(FineIOAccessor.INSTANCE, "accessor").createFile(any(Connector.class), any(URI.class), any(Model.class))).thenReturn(mock(IAppendFile.class));

        ByteFineIoWriter.build(location, false);
        IntFineIoWriter.build(location, false);
        LongFineIoWriter.build(location, false);
        DoubleFineIoWriter.build(location, false);
    }

    @Test
    public void put() {
        ByteFineIoWriter.build(location, true).put(0, (byte) 1);
        IntFineIoWriter.build(location, true).put(0, 1);
        LongFineIoWriter.build(location, true).put(0, 1);
        DoubleFineIoWriter.build(location, true).put(0, 1);

        IOAccessor accessor = Whitebox.getInternalState(FineIOAccessor.INSTANCE, "accessor");
        verify(accessor).put((IWriteFile<? extends ByteBuf>) ioFile, 0, (byte) 1);
        verify(accessor).put((IWriteFile<? extends IntBuf>) ioFile, 0, 1);
        verify(accessor).put((IWriteFile<? extends LongBuf>) ioFile, 0, 1);
        verify(accessor).put((IWriteFile<? extends DoubleBuf>) ioFile, 0, 1);
    }
}