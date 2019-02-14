package com.fr.swift.node;

import com.fr.swift.property.SwiftProperty;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class})
public class SwiftClusterNodeManagerTest extends TestCase {

    @Override
    public void setUp() {
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = PowerMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        EasyMock.expect(swiftProperty.getMasterAddress()).andReturn("127.0.0.1:8080").anyTimes();
        EasyMock.expect(swiftProperty.getServerAddress()).andReturn("127.0.0.1:8081").anyTimes();
        EasyMock.expect(swiftProperty.isCluster()).andReturn(true).anyTimes();
        PowerMock.replay(SwiftProperty.class);
        EasyMock.replay(swiftProperty);

    }

    public void testManager() {
        assertNotNull(SwiftClusterNodeManager.getInstance());
        assertEquals(SwiftClusterNodeManager.getInstance().getCurrentId(), "127.0.0.1:8081");
        assertEquals(SwiftClusterNodeManager.getInstance().getMasterId(), "127.0.0.1:8080");
        assertTrue(SwiftClusterNodeManager.getInstance().isCluster());
        assertFalse(SwiftClusterNodeManager.getInstance().isMaster());
        assertNotNull(SwiftClusterNodeManager.getInstance().getCurrentNode());
        assertNotNull(SwiftClusterNodeManager.getInstance().getMasterNode());
        SwiftClusterNodeManager.getInstance().setMasterNode(null);
        assertNull(SwiftClusterNodeManager.getInstance().getMasterNode());
        SwiftClusterNodeManager.getInstance().setCluster(false);
        assertFalse(SwiftClusterNodeManager.getInstance().isCluster());

    }
}
