//package com.fr.swift.query;
//
//import SwiftContext;
//import ProxyFactory;
//import ProxySelector;
//import BeanFactory;
//import GroupQueryInfoBean;
//import QueryBeanFactory;
//import QueryBean;
//import QueryResultSetSerializer;
//import com.fr.swift.query.session.factory.SessionFactory;
//import com.fr.swift.query.session.factory.SessionFactoryImpl;
//import SwiftResultSet;
//import QueryResultSet;
//import ServiceContext;
//import org.easymock.EasyMock;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.powermock.api.easymock.PowerMock;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.util.UUID;
//
//import static org.junit.Assert.assertEquals;
//import static org.powermock.api.mockito.PowerMockito.when;
//
///**
// * Created by lyon on 2019/1/14.
// */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SwiftContext.class, ProxySelector.class, QueryResultSetSerializer.class})
//public class QueryRunnerProviderTest {
//
//    private ServiceContext service;
//
//    @Before
//    public void setUp() {
//        PowerMock.mockStatic(SwiftContext.class);
//        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
//        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
//        EasyMock.expect(beanFactory.getBean("swiftQuerySessionFactory", SessionFactory.class))
//                .andReturn(new SessionFactoryImpl()).anyTimes();
//        PowerMock.mockStatic(ProxySelector.class);
//        ProxySelector selector = EasyMock.mock(ProxySelector.class);
//        ProxyFactory factory = EasyMock.mock(ProxyFactory.class);
//        service = Mockito.mock(ServiceContext.class);
//        EasyMock.expect(factory.getProxy(ServiceContext.class)).andReturn(service).anyTimes();
//        EasyMock.expect(selector.getFactory()).andReturn(factory).anyTimes();
//        EasyMock.expect(ProxySelector.getInstance()).andReturn(selector).anyTimes();
//        PowerMock.replay(SwiftContext.class, ProxySelector.class, beanFactory, factory, selector);
//    }
//
//    @Test
//    public void query() throws Exception {
//        QueryBean bean = GroupQueryInfoBean.builder("test").build();
//        String json = QueryBeanFactory.queryBean2String(bean);
//        QueryResultSet qrs = EasyMock.mock(QueryResultSet.class);
//        SwiftResultSet resultSet = EasyMock.mock(SwiftResultSet.class);
//        SwiftResultSet resultSet2 = EasyMock.mock(SwiftResultSet.class);
//        when(service.getQueryResult(json)).thenReturn(qrs);
//        PowerMock.mockStatic(QueryResultSetSerializer.class);
//        EasyMock.expect(QueryResultSetSerializer.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
//                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet).times(1);
//        EasyMock.expect(QueryResultSetSerializer.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
//                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet2).times(1);
//        EasyMock.expect(QueryResultSetSerializer.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
//                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet).anyTimes();
//        PowerMock.replay(QueryResultSetSerializer.class, qrs, resultSet);
//
//        // 不缓存
//        bean.setQueryId(null);
//        json = QueryBeanFactory.queryBean2String(bean);
//        when(service.getQueryResult(json)).thenReturn(qrs);
//        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
//        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet2);
//
//        bean.setQueryId(UUID.randomUUID().toString());
//        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
//        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
//    }
//}