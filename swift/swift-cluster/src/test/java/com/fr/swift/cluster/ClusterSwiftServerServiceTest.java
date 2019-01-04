package com.fr.swift.cluster;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.cluster.service.ClusterSwiftServerService;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.service.AnalyseService;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class created on 2019/1/3
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftProperty.class, SwiftContext.class})
public class ClusterSwiftServerServiceTest extends TestCase {

    @Override
    public void setUp() {
        PowerMock.mockStatic(SwiftProperty.class);
        SwiftProperty swiftProperty = EasyMock.createMock(SwiftProperty.class);
        EasyMock.expect(SwiftProperty.getProperty()).andReturn(swiftProperty).anyTimes();
        EasyMock.expect(swiftProperty.getServerAddress()).andReturn("127.0.0.1:8081").anyTimes();
        PowerMock.replay(SwiftProperty.class);
        EasyMock.replay(swiftProperty);

        SwiftServiceInfoService swiftServiceInfoService = EasyMock.createMock(SwiftServiceInfoService.class);
        EasyMock.expect(swiftServiceInfoService.getAllServiceInfo()).andReturn(new ArrayList<SwiftServiceInfoBean>()).anyTimes();
        EasyMock.expect(swiftServiceInfoService.saveOrUpdate(EasyMock.anyObject(SwiftServiceInfoBean.class))).andReturn(true).anyTimes();
        EasyMock.expect(swiftServiceInfoService.removeServiceInfo(EasyMock.anyObject(SwiftServiceInfoBean.class))).andReturn(true).anyTimes();
        EasyMock.replay(swiftServiceInfoService);

        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        EasyMock.expect(beanFactory.getBean(SwiftServiceInfoService.class)).andReturn(swiftServiceInfoService).anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.replay(beanFactory);

    }


    public void testClusterSwiftServerService() {
        SwiftService swiftService = EasyMock.createMock(SwiftService.class);
        EasyMock.expect(swiftService.getServiceType()).andReturn(ServiceType.ANALYSE).anyTimes();
        EasyMock.expect(swiftService.getId()).andReturn("127.0.0.1:8080").anyTimes();
        EasyMock.replay(swiftService);

        ClusterSwiftServerService.getInstance().start();
        ClusterSwiftServerService.getInstance().initService();
        assertTrue(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).isEmpty());

        assertEquals(ClusterSwiftServerService.getInstance().getServiceType(), ServiceType.SERVER);
        assertEquals(ClusterSwiftServerService.getInstance().trigger(null), null);
        ClusterSwiftServerService.getInstance().registerService(swiftService);
        Map<String, ClusterEntity> clusterEntityMap = ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE);
        ClusterEntity clusterEntity = clusterEntityMap.get("127.0.0.1:8080");
        assertNotNull(clusterEntity);
        assertEquals(clusterEntity.getServiceType(), ServiceType.ANALYSE);
        assertEquals(clusterEntity.getServiceClass(), AnalyseService.class);

        ClusterSwiftServerService.getInstance().offline("127.0.0.1:8080");
        assertTrue(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).isEmpty());
        ClusterSwiftServerService.getInstance().online("127.0.0.1:8080");
        assertFalse(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).isEmpty());

        ClusterSwiftServerService.getInstance().unRegisterService(swiftService);
        assertTrue(ClusterSwiftServerService.getInstance().getClusterEntityByService(ServiceType.ANALYSE).isEmpty());
    }
}
