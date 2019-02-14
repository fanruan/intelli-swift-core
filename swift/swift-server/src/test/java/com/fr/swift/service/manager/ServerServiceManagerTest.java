package com.fr.swift.service.manager;

import com.fr.swift.service.ServerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
@PrepareForTest({})
public class ServerServiceManagerTest {

    @Mock
    ServerService serverService;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testServerServiceManager() throws Exception {
        ServerServiceManager serverServiceManager = new ServerServiceManager();
        serverServiceManager.registerService(serverService);
        Mockito.verify(serverService).startServerService();
        serverServiceManager.unregisterService(serverService);
        Mockito.verify(serverService).stopServerService();
    }
}
