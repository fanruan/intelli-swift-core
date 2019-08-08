package com.fr.swift.cube.io.location;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.context.ContextProvider;
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

        ContextProvider cubePathService = mock(ContextProvider.class);
        when(SwiftContext.get().getBean(ContextProvider.class)).thenReturn(cubePathService);
        when(cubePathService.getContextPath()).thenReturn("1", "2");

        final SwiftConfig config = mock(SwiftConfig.class);
        final SwiftConfigEntityQueryBus query = mock(SwiftConfigEntityQueryBus.class);
        when(config.query(SwiftConfigEntity.class)).thenReturn(query);
        when(query.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, "")).thenReturn("1", "2");

        assertEquals("1", Whitebox.getInternalState(new ResourceLocation("a"), "basePath"));
        assertEquals("2", Whitebox.getInternalState(new ResourceLocation("b"), "basePath"));
    }
}