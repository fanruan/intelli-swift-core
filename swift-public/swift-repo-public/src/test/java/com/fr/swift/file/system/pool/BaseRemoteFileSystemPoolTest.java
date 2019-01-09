package com.fr.swift.file.system.pool;

import com.fr.swift.file.system.SwiftFileSystem;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-08
 */
public class BaseRemoteFileSystemPoolTest {
    private BaseRemoteFileSystemPool pool;

    @Before
    public void setUp() throws Exception {
        pool = new BaseRemoteFileSystemPool(new BaseRemoteSystemPoolFactory() {
            @Override
            public Object create(Object o) throws Exception {
                // Generate by Mock Plugin
                SwiftFileSystem mockSwiftFileSystem = PowerMock.createMock(SwiftFileSystem.class);
                PowerMock.replayAll();
                return mockSwiftFileSystem;
            }

        });
    }

    @Test
    public void borrowAndReturnObject() {
        SwiftFileSystem fileSystem = pool.borrowObject("test");
        boolean exception = false;
        try {
            pool.borrowObject(null);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        assertNotNull(fileSystem);
        pool.returnObject("test", fileSystem);

        PowerMock.verifyAll();
    }

}