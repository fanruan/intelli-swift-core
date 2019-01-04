package com.fr.swift.cube.io;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.cube.io.impl.fineio.connector.ConnectorProvider;
import com.fr.swift.test.TestResource;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Random;

/**
 * @author anchore
 * @date 2017/11/6
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public abstract class BaseIoTest {
    final Random r = new Random();
    static final int BOUND = 100000;
    public String cubesPath = TestResource.getRunPath(getClass()) + "cubes/table/seg0/column";

    long pos = 0;

    @Before
    public void mock() {
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory factory = EasyMock.mock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(factory).anyTimes();
        PowerMock.replay(SwiftContext.class);

        SwiftCubePathService pathService = EasyMock.mock(SwiftCubePathService.class);
        EasyMock.expect(factory.getBean(SwiftCubePathService.class)).andReturn(pathService).anyTimes();

        EasyMock.expect(pathService.getSwiftPath()).andReturn(TestResource.getRunPath(getClass())).anyTimes();

        EasyMock.expect(factory.getBean(ConnectorProvider.class)).andReturn(null);

        EasyMock.replay(factory, pathService);
    }

    @Test
    public abstract void testOverwritePutThenGet();

    @Test
    public abstract void testPutThenGet();

    @Test
    public abstract void testMemPutThenGet();
}
