package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.Destination;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.SwiftInvocation;
import com.fr.swift.basics.base.SwiftResult;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SegLocationBean;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.cube.io.Types;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * This class created on 2019/1/24
 *
 * @author Lucifer
 * @description
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, UrlSelector.class, ProxySelector.class})
@ProxyService(SwiftCollateProcessHandlerTest.class)
public class SwiftCollateProcessHandlerTest {

    List<SegmentKey> segmentKeyList;
    @Mock
    InvokerCreator invokerCreator;
    @Mock
    BeanFactory beanFactory;
    @Mock
    SwiftSegmentLocationService locationService;
    @Mock
    UrlSelector urlSelector;
    @Mock
    UrlFactory urlFactory;
    @Mock
    URL cluster1;
    @Mock
    Destination destination1;
    @Mock
    URL cluster2;
    @Mock
    Destination destination2;

    @Mock
    Invoker invoker;

    @Before
    public void setUp() throws Exception {
        segmentKeyList = new ArrayList<SegmentKey>();
        List<SegLocationBean> segLocationBeanList = new ArrayList<SegLocationBean>();
        prepareSegments(segmentKeyList, segLocationBeanList);

        PowerMockito.mockStatic(SwiftContext.class, UrlSelector.class);
        Mockito.when(SwiftContext.get()).thenReturn(beanFactory);
        Mockito.when(UrlSelector.getInstance()).thenReturn(urlSelector);
        Mockito.when(urlSelector.getFactory()).thenReturn(urlFactory);
        Mockito.when(beanFactory.getBean(SwiftSegmentLocationService.class)).thenReturn(locationService);
        Mockito.when(locationService.findBySourceKey(Mockito.any(SourceKey.class))).thenReturn(segLocationBeanList);

        Mockito.when(cluster1.getDestination()).thenReturn(destination1);
        Mockito.when(destination1.getId()).thenReturn("cluster1");
        Mockito.when(cluster2.getDestination()).thenReturn(destination2);
        Mockito.when(destination2.getId()).thenReturn("cluster2");

        Mockito.when(urlFactory.getURL("cluster1")).thenReturn(cluster1);
        Mockito.when(urlFactory.getURL("cluster2")).thenReturn(cluster2);

    }

    @Test
    public void testProcessUrl() {
        SwiftCollateProcessHandler handler = new SwiftCollateProcessHandler(invokerCreator);
        Map<URL, List<SegmentKey>> resultMap = handler.processUrl(new Target[]{Target.NONE}, new SourceKey("test"), segmentKeyList);
        assertEquals(segmentKeyList.size(), 12);
        assertEquals(resultMap.size(), 1);
        assertEquals(resultMap.get(cluster1).size(), 10);
        assertFalse(resultMap.containsKey(cluster2));
    }

    @Test
    public void testProcessResult() {
        Mockito.when(invokerCreator.createAsyncInvoker(CollateService.class, cluster1)).thenReturn(invoker);
        Mockito.when(invoker.invoke(Mockito.any(SwiftInvocation.class))).thenReturn(new SwiftResult("test" + segmentKeyList.size()));

        SwiftCollateProcessHandler handler = new SwiftCollateProcessHandler(invokerCreator);
        Object[] args = {new SourceKey("test"), segmentKeyList};
        try {
            Method method = SwiftCollateProcessHandlerTest.class.getMethod("testAppointCollate", SourceKey.class, List.class);
            String result = (String) handler.processResult(method, new Target[]{Target.NONE}, args);
            Mockito.verify(invoker).invoke(Mockito.any(SwiftInvocation.class));
            assertNull(result);
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public String testAppointCollate(SourceKey tableKey, List<SegmentKey> segmentKeyList) throws Exception {
        return null;
    }


    void prepareSegments(List<SegmentKey> segmentKeyList, List<SegLocationBean> segLocationBeanList) {
        segmentKeyList.add(new SegmentKeyBean("test", 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 3, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 4, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 5, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 6, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 7, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 8, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 9, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 10, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));
        segmentKeyList.add(new SegmentKeyBean("test", 11, Types.StoreType.FINE_IO, SwiftDatabase.CUBE));

        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(0).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(1).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(2).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(3).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(4).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(5).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(6).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(7).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(8).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster1", segmentKeyList.get(9).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster2", segmentKeyList.get(10).getId(), "test"));
        segLocationBeanList.add(new SegLocationBean("cluster2", segmentKeyList.get(11).getId(), "test"));
    }

}
