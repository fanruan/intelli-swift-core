package com.fr.swift.cube.io.location;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author anchore
 * @date 2019/3/8
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class ResourceLocationTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBugfix() {
        // REPORT-12630
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));

        SwiftCubePathService cubePathService = mock(SwiftCubePathService.class);
        when(SwiftContext.get().getBean(SwiftCubePathService.class)).thenReturn(cubePathService);
        when(cubePathService.getSwiftPath()).thenReturn("1", "2");

        assertEquals("1", Whitebox.getInternalState(new ResourceLocation("a"), "basePath"));
        assertEquals("2", Whitebox.getInternalState(new ResourceLocation("b"), "basePath"));
    }
}