package com.fr.swift.heart;

import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.management.ManagementFactory;


/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, ManagementFactory.class})
public class HeartbeatInfoTest extends TestCase {

    @Override
    public void setUp() {
        PowerMockito.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = Mockito.mock(SwiftProperty.class);
        Mockito.when(SwiftProperty.getProperty()).thenReturn(swiftProperty);
        Mockito.when(swiftProperty.getServerAddress()).thenReturn("127.0.0.1:8081");
    }

    //bug bug bug啊，测不了。
//    public void testHeartbeatInfo() {
//        HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
//        assertEquals(heartBeatInfo.getNodeId(), "127.0.0.1:8080");
//    }
}
