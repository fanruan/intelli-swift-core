package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftMetaDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

/**
 * This class created on 2019/1/15
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class})
public class BaseBeanServiceTest {

    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftMetaDataService swiftMetaDataService;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        Mockito.when(SwiftContext.get()).thenReturn(swiftContext);
        Mockito.when(swiftContext.getBean(SwiftMetaDataService.class)).thenReturn(swiftMetaDataService);
    }

    @Test
    public void testBase() {
        SwiftBaseService baseService = new SwiftBaseService();
        String[] strings = new String[0];
        baseService.cleanMetaCache(strings);
        Mockito.verify(swiftMetaDataService).cleanCache(strings);
    }
}
