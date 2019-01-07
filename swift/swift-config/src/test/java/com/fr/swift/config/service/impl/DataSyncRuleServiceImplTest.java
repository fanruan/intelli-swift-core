package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.DataSyncRuleService;
import com.fr.swift.config.service.SwiftConfigService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class DataSyncRuleServiceImplTest extends BaseServiceTest {
    private DataSyncRuleService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);

        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftConfigBean.TYPE);

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigDaoImpl.class)).andReturn(new SwiftConfigDaoImpl()).anyTimes();
        // Generate by Mock Plugin
        DataSyncRule mockDataSyncRule = PowerMock.createMock(DataSyncRule.class);

        EasyMock.expect(mockSwiftContext.getBean("defaultDataSyncRule", DataSyncRule.class)).andReturn(mockDataSyncRule).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigService.class)).andReturn(
                (SwiftConfigService) Proxy.newProxyInstance(DataSyncRuleServiceImplTest.class.getClassLoader(),
                        new Class[]{SwiftConfigService.class}, new InvocationHandler() {
                            private SwiftConfigService service;

                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                if (service == null) {
                                    service = new SwiftConfigServiceImpl();
                                }
                                return method.invoke(service, args);
                            }
                        }));
        PowerMock.replay(mockSwiftContext, mockDataSyncRule);
        service = new DataSyncRuleServiceImpl();
    }

    @Test
    public void testGetCurrentRule() {
        DataSyncRule rule = service.getCurrentRule();
        assertNotNull(rule);
        PowerMock.verifyAll();
    }

    @Test
    public void testSetCurrentRule() {
        // Generate by Mock Plugin
        DataSyncRule mockDataSyncRule = PowerMock.createMock(DataSyncRule.class);
        PowerMock.replayAll();
        service.setCurrentRule(mockDataSyncRule);
        PowerMock.verifyAll();

    }
}