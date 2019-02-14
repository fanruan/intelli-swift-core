package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.dao.impl.SwiftServiceInfoDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftServiceInfoServiceImplTest extends BaseServiceTest {

    private SwiftServiceInfoService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);


        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftServiceInfoBean.TYPE, mockData());

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftServiceInfoDao.class))).andReturn(new SwiftServiceInfoDaoImpl()).anyTimes();
        PowerMock.replay(mockSwiftContext);
        service = new SwiftServiceInfoServiceImpl();
    }

    @Test
    public void saveOrUpdate() {
        assertTrue(service.saveOrUpdate(new SwiftServiceInfoBean("service", "clusterId", "serviceInfo")));
        PowerMock.verifyAll();
    }

    @Test
    public void removeServiceInfo() {
        assertTrue(service.removeServiceInfo(new SwiftServiceInfoBean("service", "clusterId", "serviceInfo")));
        PowerMock.verifyAll();
    }

    @Test
    public void getServiceInfo() {
        assertNull(service.getServiceInfo(new SwiftServiceInfoBean("service", "clusterId", "serviceInfo")));
        PowerMock.verifyAll();
    }

    @Test
    public void getAllServiceInfo() {
        assertFalse(service.getAllServiceInfo().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void getServiceInfoByService() {
        assertFalse(service.getServiceInfoByService("service").isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void find() {
        assertFalse(service.find().isEmpty());
        PowerMock.verifyAll();
    }

    private ObjectConverter[] mockData() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SwiftServiceInfoBean mockSwiftTablePathBean = PowerMock.createMock(SwiftServiceInfoBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        ObjectConverter tablePathB = PowerMock.createMock(ObjectConverter.class);
        SwiftServiceInfoBean mockSwiftTablePathBean1 = PowerMock.createMock(SwiftServiceInfoBean.class);
        EasyMock.expect(mockSwiftTablePathBean1.convert()).andReturn(tablePathB).anyTimes();
        EasyMock.expect(tablePathB.convert()).andReturn(mockSwiftTablePathBean1).anyTimes();
        PowerMock.replay(tablePathA, tablePathB, mockSwiftTablePathBean, mockSwiftTablePathBean1);
        return new ObjectConverter[]{
                tablePathA, tablePathB
        };
    }
}