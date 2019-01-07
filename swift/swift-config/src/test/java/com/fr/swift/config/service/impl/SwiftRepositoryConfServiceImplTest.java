package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SegmentDestSelectRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.repository.SwiftFileSystemConfig;
import com.fr.swift.service.SwiftRepositoryConfService;
import com.fr.swift.service.SwiftRepositoryConfService.ConfChangeListener;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static junit.framework.TestCase.assertNull;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfChangeListener.class, SwiftContext.class})
public class SwiftRepositoryConfServiceImplTest extends BaseServiceTest {
    private SwiftRepositoryConfService service;

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
        SegmentDestSelectRule mockDataSyncRule = PowerMock.createMock(SegmentDestSelectRule.class);

        EasyMock.expect(mockSwiftContext.getBean("defaultSegmentDestSelectRule", SegmentDestSelectRule.class)).andReturn(mockDataSyncRule).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigService.class)).andReturn(
                (SwiftConfigService) Proxy.newProxyInstance(SwiftRepositoryConfServiceImplTest.class.getClassLoader(),
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
        service = new SwiftRepositoryConfServiceImpl();
    }

    @Test
    public void getCurrentRepository() {
        assertNull(service.getCurrentRepository());
        PowerMock.verifyAll();
    }

    @Test
    public void setCurrentRepository() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(ConfChangeListener.class);
        ConfChangeListener mockConfChangeListener = PowerMock.createMock(ConfChangeListener.class);
        mockConfChangeListener.change(EasyMock.anyObject(SwiftFileSystemConfig.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                return null;
            }
        }).once();
        // Generate by Mock Plugin
        SwiftFileSystemConfig mockSwiftFileSystemConfig = PowerMock.createMock(SwiftFileSystemConfig.class);

        PowerMock.replayAll();
        service.registerListener(mockConfChangeListener);
        service.setCurrentRepository(mockSwiftFileSystemConfig);
// do test
        PowerMock.verifyAll();

    }
}