package com.fr.swift.cube.io.impl.fineio.output;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectorManager.class, FineIO.class, IOFile.class})
public class PrimitiveFineIoWriterTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");


    @Before
    public void setUp() throws Exception {
        mockStatic(ConnectorManager.class);
        ConnectorManager connectorManager = mock(ConnectorManager.class);
        when(ConnectorManager.getInstance()).thenReturn(connectorManager);

        Connector connector = mock(Connector.class);
        when(connectorManager.getConnector()).thenReturn(connector);

        mockStatic(FineIO.class);
        IOFile ioFile = mock(IOFile.class);
        when(FineIO.createIOFile(Matchers.<Connector>any(), Matchers.<URI>any(), Matchers.<MODEL>any())).thenReturn(ioFile);

        doNothing().when(FineIO.class, "put", Matchers.<IOFile<ByteBuffer>>any(), anyLong(), anyByte());
        doNothing().when(FineIO.class, "put", Matchers.<IOFile<IntBuffer>>any(), anyLong(), anyInt());
        doNothing().when(FineIO.class, "put", Matchers.<IOFile<LongBuffer>>any(), anyLong(), anyLong());
        doNothing().when(FineIO.class, "put", Matchers.<IOFile<DoubleBuffer>>any(), anyLong(), anyDouble());

        when(ioFile.exists()).thenReturn(true);
    }

    @Test
    public void release() {
        ByteFineIoWriter.build(location, true).release();
        IntFineIoWriter.build(location, true).release();
        LongFineIoWriter.build(location, true).release();
        DoubleFineIoWriter.build(location, true).release();
    }

    @Test
    public void build() {
        ByteFineIoWriter.build(location, true);
        IntFineIoWriter.build(location, true);
        LongFineIoWriter.build(location, true);
        DoubleFineIoWriter.build(location, true);

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
    }
}