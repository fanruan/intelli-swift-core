package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.FineIOConnectorConfig;
import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftFineIOConnectorService;
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
public class SwiftFineIOConfigServiceImplTest extends BaseServiceTest {

    private SwiftFineIOConnectorService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);

        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftConfigEntity.class);

        BaseTransactionManager mockBaseTransactionManager = new BaseTransactionManager() {
            @Override
            protected ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(TransactionManager.class))).andReturn(mockBaseTransactionManager).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigDaoImpl.class)).andReturn(new SwiftConfigDaoImpl()).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigService.class)).andReturn(
                (SwiftConfigService) Proxy.newProxyInstance(SwiftFineIOConfigServiceImplTest.class.getClassLoader(),
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
        PowerMock.replay(mockSwiftContext);
        service = new SwiftFineIOConfigServiceImpl();
    }

    @Test
    public void getCurrentConfig() {
        assertNotNull(service.getCurrentConfig(SwiftFineIOConnectorService.Type.CONNECTOR));
        PowerMock.verifyAll();
    }

    @Test
    public void setCurrentConfig() {
        // Generate by Mock Plugin
        FineIOConnectorConfig mockFineIOConnectorConfig = PowerMock.createMock(FineIOConnectorConfig.class);
        PowerMock.replayAll();
        service.setCurrentConfig(mockFineIOConnectorConfig, SwiftFineIOConnectorService.Type.CONNECTOR);
// do test
        PowerMock.verifyAll();

    }
}