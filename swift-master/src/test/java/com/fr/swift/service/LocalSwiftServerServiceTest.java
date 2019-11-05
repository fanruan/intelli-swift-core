package com.fr.swift.service;

import com.fr.swift.db.Where;
import com.fr.swift.event.base.AbstractGlobalRpcEvent;
import com.fr.swift.event.base.SwiftRpcEvent;
import com.fr.swift.event.global.DeleteEvent;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.listener.SwiftServiceListenerManager;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
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

import static org.junit.Assert.assertEquals;

/**
 * Created by pony on 2017/11/14.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftServiceListenerManager.class, SwiftProperty.class})
public class LocalSwiftServerServiceTest {
    @Mock
    RealtimeService realTimeService;
    @Mock
    HistoryService historyService;
    @Mock
    AnalyseService analyseService;
    @Mock
    CollateService collateService;
    @Mock
    DeleteService deleteService;
    @Mock
    UploadService uploadService;
    @Mock
    SwiftServiceListenerManager swiftServiceListenerManager;
    @Mock
    DeleteEvent event;
    @Mock
    SwiftProperty swiftProperty;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftServiceListenerManager.class, SwiftProperty.class);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(swiftProperty.isCluster()).thenReturn(false);
        Mockito.when(SwiftServiceListenerManager.getInstance()).thenReturn(swiftServiceListenerManager);
        Mockito.when(realTimeService.getServiceType()).thenReturn(ServiceType.REAL_TIME);
        Mockito.when(historyService.getServiceType()).thenReturn(ServiceType.HISTORY);
        Mockito.when(analyseService.getServiceType()).thenReturn(ServiceType.ANALYSE);
        Mockito.when(collateService.getServiceType()).thenReturn(ServiceType.COLLATE);
        Mockito.when(deleteService.getServiceType()).thenReturn(ServiceType.DELETE);
        Mockito.when(uploadService.getServiceType()).thenReturn(ServiceType.UPLOAD);
        Mockito.when(event.type()).thenReturn(SwiftRpcEvent.EventType.GLOBAL);
        Mockito.when(event.subEvent()).thenReturn(AbstractGlobalRpcEvent.Event.DELETE);
        Pair<SourceKey, Where> content = Mockito.mock(Pair.class);
        Mockito.when(content.getKey()).thenReturn(Mockito.mock(SourceKey.class));
        Mockito.when(content.getValue()).thenReturn(Mockito.mock(Where.class));
        Mockito.when(event.getContent()).thenReturn(content);

    }

    @Test
    public void testLocalSwiftServerService() throws Exception {
        LocalSwiftServerService swiftServerService = new LocalSwiftServerService();
        swiftServerService.start();
        assertEquals(swiftServerService.getServiceType(), ServiceType.SERVER);

        Mockito.verify(swiftServiceListenerManager).registerHandler(swiftServerService);
        swiftServerService.registerService(realTimeService);
        swiftServerService.registerService(historyService);
        swiftServerService.registerService(analyseService);
        swiftServerService.registerService(collateService);
        swiftServerService.registerService(deleteService);
        swiftServerService.registerService(uploadService);
        Mockito.verify(realTimeService, Mockito.times(1)).getServiceType();
        Mockito.verify(historyService, Mockito.times(1)).getServiceType();
        Mockito.verify(analyseService, Mockito.times(1)).getServiceType();
        Mockito.verify(collateService, Mockito.times(1)).getServiceType();
        Mockito.verify(deleteService, Mockito.times(1)).getServiceType();
        Mockito.verify(uploadService, Mockito.times(1)).getServiceType();

        swiftServerService.trigger(event);
        Mockito.verify(deleteService).delete(Mockito.any(SourceKey.class), Mockito.any(Where.class));

        swiftServerService.unRegisterService(realTimeService);
        swiftServerService.unRegisterService(historyService);
        swiftServerService.unRegisterService(analyseService);
        swiftServerService.unRegisterService(collateService);
        swiftServerService.unRegisterService(deleteService);
        swiftServerService.unRegisterService(uploadService);
        Mockito.verify(realTimeService, Mockito.times(2)).getServiceType();
        Mockito.verify(historyService, Mockito.times(2)).getServiceType();
        Mockito.verify(analyseService, Mockito.times(2)).getServiceType();
        Mockito.verify(collateService, Mockito.times(2)).getServiceType();
        Mockito.verify(deleteService, Mockito.times(2)).getServiceType();
        Mockito.verify(uploadService, Mockito.times(2)).getServiceType();
    }
}