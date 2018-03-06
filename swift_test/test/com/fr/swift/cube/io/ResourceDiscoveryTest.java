package com.fr.swift.cube.io;

import com.fr.swift.cube.io.Types.DataType;
import com.fr.swift.cube.io.Types.IoType;
import com.fr.swift.cube.io.impl.fineio.output.ByteFineIoWriter;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.cube.io.output.Writer;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.fr.swift.cube.io.BaseIoTest.CUBES_PATH;

/**
 * @author anchore
 * @date 2017/11/21
 */
public class ResourceDiscoveryTest extends TestCase {
    private static final ResourceDiscovery DISCOVERY = ResourceDiscoveryImpl.getInstance();
    private ExecutorService exec = Executors.newFixedThreadPool(8);

    public void testGetReader() throws ExecutionException, InterruptedException {
        List<Future<Reader>> readers = new ArrayList<>();
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/byte/child");
        for (int i = 0; i < 16; i++) {
            readers.add(exec.submit(() -> DISCOVERY.getReader(location, new BuildConf(IoType.READ, DataType.BYTE))));
        }

        for (int i = 0; i < readers.size() - 1; i++) {
            if (readers.get(i).get() != readers.get(i + 1).get()) {
                fail();
            }
        }
    }

    public void testGetWriter() throws ExecutionException, InterruptedException {
        ExecutorService exec = Executors.newFixedThreadPool(8);
        List<Future<Writer>> writers = new ArrayList<>();
        IResourceLocation location = new ResourceLocation(CUBES_PATH + "/byte/child");
        for (int i = 0; i < 16; i++) {
            writers.add(exec.submit(() -> DISCOVERY.getWriter(location, new BuildConf(IoType.WRITE, DataType.BYTE))));
        }
        for (int i = 0; i < writers.size() - 1; i++) {
            if (writers.get(i).get() != writers.get(i + 1).get()) {
                fail();
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        exec.shutdown();
    }
}
