package com.fr.swift.exception.inspect;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.SwiftContext;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.exception.inspect.bean.RpcHealthInfoBean;
import com.fr.swift.exception.inspect.service.ComponentHealthInspectService;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.ServiceType;
import com.fr.swift.service.SwiftService;
import com.fr.swift.service.bean.RpcHealthResultBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;

/**
 * @author Marvin
 * @version 1.1
 * Created by Marvin on 9/3/2019
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ClusterSelector.class, RpcServiceHealthInspector.class, ProxySelector.class, ComponentHealthInspectService.class})
public class RpcServiceHealthInspectorTest {

    RpcServiceHealthInspector inspector;
    SwiftService service;
    ClusterNodeManager manager;
    RpcHealthInfoBean info;
    ServiceType target;
    RpcHealthResultBean bean;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ClusterSelector.class);
        PowerMockito.mockStatic(RpcServiceHealthInspector.class);
        PowerMockito.mockStatic(ProxySelector.class);
        PowerMockito.mockStatic(SwiftContext.class);

        ClusterSelector selector = PowerMockito.mock(ClusterSelector.class);
        PowerMockito.when(ClusterSelector.getInstance()).thenReturn(selector);
        manager = PowerMockito.mock(ClusterNodeManager.class);
        PowerMockito.when(selector.getFactory()).thenReturn(manager);

        target = PowerMockito.mock(ServiceType.class);
        info = PowerMockito.mock(RpcHealthInfoBean.class);
        bean = PowerMockito.mock(RpcHealthResultBean.class);

        PowerMockito.when(info.getTarget()).thenReturn(target);
        PowerMockito.when(info.isInspectOtherSlave()).thenReturn(false);

        ComponentHealthInspectService service = PowerMockito.mock(ComponentHealthInspectService.class);
        PowerMockito.when(ProxySelector.getProxy(ComponentHealthInspectService.class)).thenReturn(service);
        PowerMockito.when(service.inspectSlaveRpcHealth(target)).thenReturn(Collections.singleton(bean));

        BeanFactory beanFactory = PowerMockito.mock(BeanFactory.class);
        PowerMockito.when(SwiftContext.get()).thenReturn(beanFactory);
        PowerMockito.when(beanFactory.getBean(ComponentHealthInspectService.class)).thenReturn(service);
        PowerMockito.when(service.inspectMasterRpcHealth(this.target)).thenReturn(Collections.singleton(bean));
    }

    @Test
    public void testInspect() {
        inspector = PowerMockito.spy(new RpcServiceHealthInspector());

        PowerMockito.when(manager.isMaster()).thenReturn(true);
        Assert.assertEquals(inspector.inspect(info), Collections.singleton("127.0.0.1:8000"));

        PowerMockito.when(manager.isMaster()).thenReturn(false);
        Assert.assertEquals(inspector.inspect(info), Collections.singleton("127.0.0.1:7000"));

    }
}
