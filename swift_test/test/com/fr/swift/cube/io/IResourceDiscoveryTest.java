package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import com.fr.swift.test.TestIo;
import org.junit.After;
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
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/int/child", StoreType.MEMORY);
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
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/int/child", StoreType.MEMORY);
        for (int i = 0; i < 16; i++) {
            writers.add(exec.submit(() -> DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.INT))));
        }
        for (int i = 0; i < writers.size() - 1; i++) {
            if (writers.get(i).get() != writers.get(i + 1).get()) {
                fail();
            }
        }
    }

    @After
    public void tearDown() {
        exec.shutdown();
    }
}
