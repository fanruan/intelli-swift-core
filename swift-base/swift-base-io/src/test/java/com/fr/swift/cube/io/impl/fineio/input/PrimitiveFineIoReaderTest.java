package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.FineIO.MODEL;
import com.fineio.io.ByteBuffer;
import com.fineio.io.DoubleBuffer;
import com.fineio.io.IntBuffer;
import com.fineio.io.LongBuffer;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/1/4
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectorManager.class, FineIO.class, IOFile.class})
public class PrimitiveFineIoReaderTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");
    private final IOFile ioFile = mock(IOFile.class);


    @Before
    public void setUp() throws Exception {
        mockStatic(ConnectorManager.class);
        ConnectorManager connectorManager = mock(ConnectorManager.class);
        when(ConnectorManager.getInstance()).thenReturn(connectorManager);

        Connector connector = mock(Connector.class);
        when(connectorManager.getConnector()).thenReturn(connector);

        mockStatic(FineIO.class);
        when(FineIO.createIOFile(ArgumentMatchers.<Connector>any(), ArgumentMatchers.<URI>any(), ArgumentMatchers.<MODEL>any())).thenReturn(ioFile);

        when(FineIO.getByte(ArgumentMatchers.<IOFile<ByteBuffer>>any(), anyLong())).thenReturn((byte) 1);
        when(FineIO.getInt(ArgumentMatchers.<IOFile<IntBuffer>>any(), anyLong())).thenReturn(1);
        when(FineIO.getLong(ArgumentMatchers.<IOFile<LongBuffer>>any(), anyLong())).thenReturn(1L);
        when(FineIO.getDouble(ArgumentMatchers.<IOFile<DoubleBuffer>>any(), anyLong())).thenReturn(1D);

        when(ioFile.exists()).thenReturn(true);
    }

    @Test
    public void isReadable() {
        Assert.assertTrue(ByteFineIoReader.build(location).isReadable());
        Assert.assertTrue(IntFineIoReader.build(location).isReadable());
        Assert.assertTrue(LongFineIoReader.build(location).isReadable());
        Assert.assertTrue(DoubleFineIoReader.build(location).isReadable());
    }

    @Test
    public void release() {
        ByteFineIoReader.build(location).release();
        IntFineIoReader.build(location).release();
        LongFineIoReader.build(location).release();
        DoubleFineIoReader.build(location).release();

        verifyZeroInteractions(ioFile);
    }

    @Test
    public void build() {
        ByteFineIoReader.build(location);
        IntFineIoReader.build(location);
        LongFineIoReader.build(location);
        DoubleFineIoReader.build(location);
    }

    @Test
    public void get() {
        Assert.assertEquals(1, ByteFineIoReader.build(location).get(1));
        Assert.assertEquals(1, IntFineIoReader.build(location).get(1));
        Assert.assertEquals(1, LongFineIoReader.build(location).get(1));
        Assert.assertEquals(1, DoubleFineIoReader.build(location).get(1), 0);
    }
}