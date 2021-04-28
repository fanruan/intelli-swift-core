package com.fr.swift.cloud.cube.io.impl.fineio.input;

import com.fineio.FineIO;
import com.fineio.accessor.FineIOAccessor;
import com.fineio.accessor.IOAccessor;
import com.fineio.accessor.Model;
import com.fineio.accessor.file.IReadFile;
import com.fineio.io.file.IOFile;
import com.fineio.storage.Connector;
import com.fr.swift.cloud.cube.io.impl.fineio.connector.ConnectorManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
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
public class PrimitiveFineIoReaderTest {

    private final URI location = URI.create("/cubes/table/seg0/column/detail");
    private final IReadFile<?> ioFile = mock(IReadFile.class);

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

        when(ioAccessor.getByte(any(IReadFile.class), anyInt())).thenReturn((byte) 1);
        when(ioAccessor.getInt(any(IReadFile.class), anyInt())).thenReturn(1);
        when(ioAccessor.getLong(any(IReadFile.class), anyInt())).thenReturn(1L);
        when(ioAccessor.getDouble(any(IReadFile.class), anyInt())).thenReturn(1D);

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

        verify(ioFile, times(4)).close();
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