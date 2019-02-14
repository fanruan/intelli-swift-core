package com.fr.swift.selector;

import com.fr.swift.ClusterNodeManager;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Test;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
public class ClusterSelectorTest extends TestCase {

    @Test
    public void testClusterSelector() {
        assertNull(ClusterSelector.getInstance().getFactory());
        ClusterNodeManager clusterNodeManager = EasyMock.createMock(ClusterNodeManager.class);
        EasyMock.replay(clusterNodeManager);
        ClusterSelector.getInstance().switchFactory(clusterNodeManager);
        assertNotNull(ClusterSelector.getInstance().getFactory());
        assertEquals(clusterNodeManager, ClusterSelector.getInstance().getFactory());
    }
}
