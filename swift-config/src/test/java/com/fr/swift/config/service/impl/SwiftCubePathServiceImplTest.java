package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.impl.SwiftConfigDaoImpl;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.TransactionManager;
import com.fr.swift.config.oper.impl.BaseTransactionManager;
import com.fr.swift.config.service.SwiftConfigService;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.config.service.SwiftCubePathService.PathChangeListener;
import com.fr.swift.context.ContextProvider;
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

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, PathChangeListener.class})
public class SwiftCubePathServiceImplTest extends BaseServiceTest {
    private SwiftCubePathService service;

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
        ContextProvider mockContextProvider = PowerMock.createMock(ContextProvider.class);
        EasyMock.expect(mockContextProvider.getContextPath()).andReturn("/root").anyTimes();

        EasyMock.expect(mockSwiftContext.getBean(ContextProvider.class)).andReturn(mockContextProvider).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(SwiftConfigService.class)).andReturn(
                (SwiftConfigService) Proxy.newProxyInstance(SwiftCubePathServiceImplTest.class.getClassLoader(),
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
        PowerMock.replay(mockSwiftContext, mockContextProvider);
        service = new SwiftCubePathServiceImpl();
    }

    @Test
    public void setSwiftPath() {
        // Generate by Mock Plugin
        PowerMock.mockStatic(PathChangeListener.class);
        PathChangeListener mockPathChangeListener = PowerMock.createMock(PathChangeListener.class);
        mockPathChangeListener.changed(EasyMock.notNull(String.class));
        EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
            @Override
            public Boolean answer() throws Throwable {
                return true;
            }
        }).once();
        PowerMock.replayAll();
        service.registerPathChangeListener(mockPathChangeListener);
        assertTrue(service.setSwiftPath("anyPath"));
        PowerMock.verifyAll();
    }

    @Test
    public void getSwiftPath() {
        assertEquals("/root", service.getSwiftPath());
        PowerMock.verifyAll();
    }
}