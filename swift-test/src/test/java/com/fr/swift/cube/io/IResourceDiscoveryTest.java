package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.io.IntIo;
import com.fr.swift.test.TestIo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;
import static org.junit.Assert.fail;

/**
 * @author anchore
 * @date 2017/11/21
 */
public class IResourceDiscoveryTest extends TestIo {
    private static final IResourceDiscovery DISCOVERY = ResourceDiscovery.getInstance();
    private ExecutorService exec = Executors.newFixedThreadPool(8);

    @Test
    public void testGetReader() throws ExecutionException, InterruptedException {
        List<Future<Reader>> readers = new ArrayList<>();
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/int/seg0/c1", StoreType.MEMORY);
        for (int i = 0; i < 16; i++) {
            readers.add(exec.submit(() -> DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.INT))));
        }

        for (int i = 0; i < readers.size() - 1; i++) {
            if (readers.get(i).get() != readers.get(i + 1).get()) {
                fail();
            }
        }
    }

    @Test
    public void testGetWriter() throws ExecutionException, InterruptedException {
        List<Future<Writer>> writers = new ArrayList<>();
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/int/seg0/c1", StoreType.MEMORY);
        for (int i = 0; i < 16; i++) {
            writers.add(exec.submit(() -> DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.INT))));
        }
        for (int i = 0; i < writers.size() - 1; i++) {
            if (writers.get(i).get() != writers.get(i + 1).get()) {
                fail();
            }
        }
    }

    @Test
    public void testRelease() {
        String segPath = "logs/cubes/table/seg0";
        String columnPath = segPath + "/column";
        ResourceLocation columnLoc = new ResourceLocation(columnPath + "/detail", StoreType.MEMORY);
        ResourceLocation columnLoc1 = new ResourceLocation(columnPath + "1/detail", StoreType.MEMORY);
        BuildConf conf = new BuildConf(IoType.WRITE, DataType.INT);

        // test release column
        ((IntIo) DISCOVERY.getWriter(columnLoc, conf)).put(0, -1);
        ((IntIo) DISCOVERY.getWriter(columnLoc1, conf)).put(0, -1);

        Assert.assertTrue(DISCOVERY.exists(columnLoc, conf));
        Assert.assertTrue(DISCOVERY.exists(columnLoc1, conf));

        DISCOVERY.release(new ResourceLocation(columnPath, StoreType.MEMORY));
        Assert.assertFalse(DISCOVERY.exists(columnLoc, conf));
        Assert.assertTrue(DISCOVERY.exists(columnLoc1, conf));

        DISCOVERY.release(new ResourceLocation(columnPath + "1", StoreType.MEMORY));

        // test release seg
        ((IntIo) DISCOVERY.getWriter(columnLoc, conf)).put(0, -1);
        ((IntIo) DISCOVERY.getWriter(columnLoc1, conf)).put(0, -1);

        Assert.assertTrue(DISCOVERY.exists(columnLoc, conf));
        Assert.assertTrue(DISCOVERY.exists(columnLoc1, conf));

        DISCOVERY.release(new ResourceLocation(segPath, StoreType.MEMORY));
        Assert.assertFalse(DISCOVERY.exists(columnLoc, conf));
        Assert.assertFalse(DISCOVERY.exists(columnLoc1, conf));
    }

    @After
    public void tearDown() {
        exec.shutdown();
    }
}
