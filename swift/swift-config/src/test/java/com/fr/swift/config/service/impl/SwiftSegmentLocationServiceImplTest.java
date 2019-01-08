package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.dao.SwiftSegmentLocationDao;
import com.fr.swift.config.dao.impl.SwiftSegmentLocationDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftSegmentLocationServiceImplTest extends BaseServiceTest {
    private SwiftSegmentLocationService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);


        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SegLocationBean.TYPE, mockData());

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftSegmentLocationDao.class))).andReturn(new SwiftSegmentLocationDaoImpl()).anyTimes();
        PowerMock.replay(mockSwiftContext);
        service = new SwiftSegmentLocationServiceImpl();
    }
    @Test
    public void delete() {
        assertTrue(service.delete("table", "clusterId"));
        PowerMock.verifyAll();
    }

    @Test
    public void findAll() {
        assertFalse(service.findAll().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void find() {
        assertFalse(service.find().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void saveOrUpdate() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SegLocationBean mockSwiftTablePathBean = PowerMock.createMock(SegLocationBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getSourceKey()).andReturn("sourceKeyA").anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        PowerMock.replayAll();
        assertTrue(service.saveOrUpdate(mockSwiftTablePathBean));
// do test
        PowerMock.verifyAll();

    }

    private ObjectConverter[] mockData() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SegLocationBean mockSwiftTablePathBean = PowerMock.createMock(SegLocationBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getSourceKey()).andReturn("sourceKeyA").anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        ObjectConverter tablePathB = PowerMock.createMock(ObjectConverter.class);
        SegLocationBean mockSwiftTablePathBean1 = PowerMock.createMock(SegLocationBean.class);
        EasyMock.expect(mockSwiftTablePathBean1.convert()).andReturn(tablePathB).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean1.getSourceKey()).andReturn("sourceKeyB").anyTimes();
        EasyMock.expect(tablePathB.convert()).andReturn(mockSwiftTablePathBean1).anyTimes();
        PowerMock.replay(tablePathA, tablePathB, mockSwiftTablePathBean, mockSwiftTablePathBean1);
        return new ObjectConverter[]{
                tablePathA, tablePathB
        };
    }
}