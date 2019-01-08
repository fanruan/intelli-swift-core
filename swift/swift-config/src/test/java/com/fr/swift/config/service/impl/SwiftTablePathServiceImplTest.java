package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftTablePathBean;
import com.fr.swift.config.dao.SwiftTablePathDao;
import com.fr.swift.config.dao.impl.SwiftTablePathDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.converter.ObjectConverter;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftTablePathServiceImplTest extends BaseServiceTest {
    private SwiftTablePathService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);


        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftTablePathBean.TYPE, mockData());

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(SwiftTablePathDao.class))).andReturn(new SwiftTablePathDaoImpl()).anyTimes();
        PowerMock.replay(mockSwiftContext);
        service = new SwiftTablePathServiceImpl();
    }

    @Test
    public void find() {
        assertFalse(service.find().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void saveOrUpdate() {
        assertTrue(service.saveOrUpdate(new SwiftTablePathBean("table", 0)));
        PowerMock.verifyAll();
    }

    @Test
    public void removePath() {
        assertTrue(service.removePath("table"));
        PowerMock.verifyAll();
    }

    @Test
    public void getTablePath() {
        assertEquals(100, service.getTablePath("table").intValue());
        PowerMock.verifyAll();
    }

    @Test
    public void getLastPath() {
        assertEquals(99, service.getLastPath("table").intValue());
        PowerMock.verifyAll();
    }

    @Test
    public void get() {
        assertNotNull(service.get("table"));
        PowerMock.verifyAll();
    }

    private ObjectConverter[] mockData() {
        // Generate by Mock Plugin
        ObjectConverter tablePathA = PowerMock.createMock(ObjectConverter.class);
        // Generate by Mock Plugin
        SwiftTablePathBean mockSwiftTablePathBean = PowerMock.createMock(SwiftTablePathBean.class);
        EasyMock.expect(mockSwiftTablePathBean.convert()).andReturn(tablePathA).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getTablePath()).andReturn(100).anyTimes();
        EasyMock.expect(mockSwiftTablePathBean.getLastPath()).andReturn(99).anyTimes();

        EasyMock.expect(tablePathA.convert()).andReturn(mockSwiftTablePathBean).anyTimes();
        ObjectConverter tablePathB = PowerMock.createMock(ObjectConverter.class);
        SwiftTablePathBean mockSwiftTablePathBean1 = PowerMock.createMock(SwiftTablePathBean.class);
        EasyMock.expect(mockSwiftTablePathBean1.convert()).andReturn(tablePathB).anyTimes();
        EasyMock.expect(tablePathB.convert()).andReturn(mockSwiftTablePathBean1).anyTimes();
        PowerMock.replay(tablePathA, tablePathB, mockSwiftTablePathBean, mockSwiftTablePathBean1);
        return new ObjectConverter[]{
                tablePathA, tablePathB
        };
    }
}