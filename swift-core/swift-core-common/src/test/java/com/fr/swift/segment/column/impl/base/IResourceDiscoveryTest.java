package com.fr.swift.segment.column.impl.base;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.io.BuildConf;
import com.fr.swift.cube.io.IntIo;
import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.fail;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2017/11/21
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class IResourceDiscoveryTest {

    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();

    @Before
    public void setUp() {
        mockStatic(SwiftContext.class);
        BeanFactory beanFactory = mock(BeanFactory.class);
        when(SwiftContext.get()).thenReturn(beanFactory);

        SwiftCubePathService service = mock(SwiftCubePathService.class);
        when(beanFactory.getBean(SwiftCubePathService.class)).thenReturn(service);

        when(service.getSwiftPath()).thenReturn("/");
    }

    @Test
    public void testGetReader() throws ExecutionException, InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(8);
        try {
            List<Future<Reader>> readers = new ArrayList<Future<Reader>>();
            final IResourceLocation location = new ResourceLocation("/cubes/table/seg0/column/int/seg0/c1", StoreType.MEMORY);
            for (int i = 0; i < 16; i++) {
                readers.add(exec.submit(new Callable<Reader>() {
                    @Override
                    public Reader call() {
                        return DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.INT));
                    }
                }));
            }

            for (int i = 0; i < readers.size() - 1; i++) {
                if (readers.get(i).get() != readers.get(i + 1).get()) {
                    fail();
                }
            }
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void testGetWriter() throws ExecutionException, InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(8);
        try {
            List<Future<Writer>> writers = new ArrayList<Future<Writer>>();
            final IResourceLocation location = new ResourceLocation("/cubes/table/seg0/column/int/seg0/c1", StoreType.MEMORY);
            for (int i = 0; i < 16; i++) {
                writers.add(exec.submit(new Callable<Writer>() {
                    @Override
                    public Writer call() {
                        return DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.INT));
                    }
                }));
            }
            for (int i = 0; i < writers.size() - 1; i++) {
                if (writers.get(i).get() != writers.get(i + 1).get()) {
                    fail();
                }
            }
        } finally {
            exec.shutdown();
        }
    }

    @Test
    public void releaseTable() {
        SwiftDatabase schema = SwiftDatabase.CUBE;
        SourceKey tableKey = new SourceKey("table");
        SourceKey tableKey1 = new SourceKey("table1");
        ColumnKey columnKey = new ColumnKey("column");

        BuildConf conf = new BuildConf(IoType.WRITE, DataType.INT);

        CubePathBuilder cubePathBuilder = new CubePathBuilder();
        // schema/table/seg0/column
        ResourceLocation location = new ResourceLocation(cubePathBuilder.setSwiftSchema(schema).setTableKey(tableKey).setSegOrder(0).setColumnId(columnKey.getName()).build(), StoreType.MEMORY);
        // schema/table1/seg0/column
        ResourceLocation location1 = new ResourceLocation(cubePathBuilder.setTableKey(tableKey1).build(), StoreType.MEMORY);

        ((IntIo) DISCOVERY.getWriter(location, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location1, conf)).put(0, 1);

        DISCOVERY.releaseTable(schema, tableKey);

        Assert.assertFalse(DISCOVERY.exists(location, conf));
        Assert.assertTrue(DISCOVERY.exists(location1, conf));
    }

    @Test
    public void releaseSeg() {
        SwiftDatabase schema = SwiftDatabase.CUBE;
        SourceKey tableKey = new SourceKey("table");
        SourceKey tableKey1 = new SourceKey("table1");
        ColumnKey columnKey = new ColumnKey("column");

        BuildConf conf = new BuildConf(IoType.WRITE, DataType.INT);

        CubePathBuilder cubePathBuilder = new CubePathBuilder();
        // schema/table/seg0/column
        ResourceLocation location = new ResourceLocation(cubePathBuilder.setSwiftSchema(schema).setTableKey(tableKey).setSegOrder(0).setColumnId(columnKey.getName()).build(), StoreType.MEMORY);
        // schema/table/seg1/column
        ResourceLocation location1 = new ResourceLocation(cubePathBuilder.setSegOrder(1).build(), StoreType.MEMORY);
        // schema/table1/seg1/column
        ResourceLocation location2 = new ResourceLocation(cubePathBuilder.setTableKey(tableKey1).build(), StoreType.MEMORY);

        ((IntIo) DISCOVERY.getWriter(location, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location1, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location2, conf)).put(0, 1);

        DISCOVERY.releaseSegment(schema, tableKey, 0);

        Assert.assertFalse(DISCOVERY.exists(location, conf));
        Assert.assertTrue(DISCOVERY.exists(location1, conf));
        Assert.assertTrue(DISCOVERY.exists(location2, conf));

    }

    @Test
    public void releaseColumn() {
        SwiftDatabase schema = SwiftDatabase.CUBE;
        SourceKey tableKey = new SourceKey("table");
        SourceKey tableKey1 = new SourceKey("table1");
        ColumnKey columnKey = new ColumnKey("column");
        ColumnKey columnKey1 = new ColumnKey("column1");

        BuildConf conf = new BuildConf(IoType.WRITE, DataType.INT);

        CubePathBuilder cubePathBuilder = new CubePathBuilder();
        // schema/table/seg0/column
        ResourceLocation location = new ResourceLocation(cubePathBuilder.setSwiftSchema(schema).setTableKey(tableKey).setSegOrder(0).setColumnId(columnKey.getName()).build(), StoreType.MEMORY);
        // schema/table/seg0/column1
        ResourceLocation location1 = new ResourceLocation(cubePathBuilder.setColumnId(columnKey1.getName()).build(), StoreType.MEMORY);

        // schema/table/seg1/column1
        ResourceLocation location2 = new ResourceLocation(cubePathBuilder.setSegOrder(1).build(), StoreType.MEMORY);
        // schema/table/seg1/column
        ResourceLocation location3 = new ResourceLocation(cubePathBuilder.setColumnId(columnKey.getName()).build(), StoreType.MEMORY);

        // schema/table1/seg0/column
        ResourceLocation location4 = new ResourceLocation(cubePathBuilder.setSwiftSchema(schema).setTableKey(tableKey1).setSegOrder(0).setColumnId(columnKey.getName()).build(), StoreType.MEMORY);
        // schema/table1/seg0/column1
        ResourceLocation location5 = new ResourceLocation(cubePathBuilder.setColumnId(columnKey1.getName()).build(), StoreType.MEMORY);

        // schema/table1/seg1/column1
        ResourceLocation location6 = new ResourceLocation(cubePathBuilder.setSegOrder(1).build(), StoreType.MEMORY);
        // schema/table1/seg1/column
        ResourceLocation location7 = new ResourceLocation(cubePathBuilder.setColumnId(columnKey.getName()).build(), StoreType.MEMORY);

        ((IntIo) DISCOVERY.getWriter(location, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location1, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location2, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location3, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location4, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location5, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location6, conf)).put(0, 1);
        ((IntIo) DISCOVERY.getWriter(location7, conf)).put(0, 1);

        DISCOVERY.releaseColumn(schema, tableKey, columnKey);

        Assert.assertFalse(DISCOVERY.exists(location, conf));
        Assert.assertTrue(DISCOVERY.exists(location1, conf));
        Assert.assertTrue(DISCOVERY.exists(location2, conf));
        Assert.assertFalse(DISCOVERY.exists(location3, conf));
        Assert.assertTrue(DISCOVERY.exists(location4, conf));
        Assert.assertTrue(DISCOVERY.exists(location5, conf));
        Assert.assertTrue(DISCOVERY.exists(location6, conf));
        Assert.assertTrue(DISCOVERY.exists(location7, conf));

    }
}
