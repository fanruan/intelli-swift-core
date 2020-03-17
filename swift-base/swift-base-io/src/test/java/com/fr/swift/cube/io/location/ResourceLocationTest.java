package com.fr.swift.cube.io.location;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.service.SwiftCubePathService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
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
        mockStatic(SwiftContext.class);
        when(SwiftContext.get()).thenReturn(mock(BeanFactory.class));
    }

    @Test
    public void testBug() {
        // REPORT-12630
        // DEC-9316 base path频繁查数据库的性能bug
        final SwiftCubePathService pathService = mock(SwiftCubePathService.class);
        when(SwiftContext.get().getBean(SwiftCubePathService.class)).thenReturn(pathService);
        when(pathService.getSwiftPath()).thenReturn("1", "q");
        final SwiftCubePathService.PathChangeListener[] pathChangeListener = {null};
        doAnswer((Answer<Void>) invocation -> {
            pathChangeListener[0] = invocation.getArgument(0);
            return null;
        }).when(pathService).registerPathChangeListener(any(SwiftCubePathService.PathChangeListener.class));

        new ResourceLocation("a");

        assertThat((String) Whitebox.getInternalState(ResourceLocation.class, "basePath")).isEqualTo("1");

        new ResourceLocation("b");

        assertThat((String) Whitebox.getInternalState(ResourceLocation.class, "basePath")).isEqualTo("1");

        pathChangeListener[0].changed("2");

        new ResourceLocation("c");

        assertThat((String) Whitebox.getInternalState(ResourceLocation.class, "basePath")).isEqualTo("2");
    }
}