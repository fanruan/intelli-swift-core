//package com.fr.swift.service;
//
//import SwiftContext;
//import URL;
//import Invoker;
//import InvokerCreator;
//import UrlFactory;
//import ProxyService;
//import Target;
//import ProxyServiceRegistry;
//import UrlSelector;
//import BeanFactory;
//import LocalInvoker;
//import QueryBuilder;
//import GroupQueryInfoBean;
//import QueryBeanFactory;
//import QueryInfoBean;
//import Query;
//import QueryBean;
//import BaseSerializedQueryResultSet;
//import GroupNode;
//import QueryResultSet;
//import QueryResultSetMerger;
//import SourceKey;
//import Pair;
//import junit.framework.TestCase;
//import org.easymock.EasyMock;
//import org.junit.runner.RunWith;
//import org.powermock.api.easymock.PowerMock;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * Created by lyon on 2019/1/7.
// */
//@RunWith(PowerMockRunner.class)
//@PrepareForTest({SwiftContext.class, SegmentLocationProvider.class, QueryBuilder.class})
//@ProxyService(SwiftQueryableProcessHandlerTest.class)
//public class SwiftQueryableProcessHandlerTest extends TestCase {
//
//    private InvokerCreator invokerCreator;
//    private SegmentLocationProvider provider;
//
//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        ProxyServiceRegistry.get().registerService(new SwiftQueryableProcessHandlerTest());
//
//        SwiftServiceInfoEntity swiftServiceInfoBean = EasyMock.createMock(SwiftServiceInfoEntity.class);
//        SwiftServiceInfoService swiftServiceInfoService = EasyMock.createMock(SwiftServiceInfoService.class);
//        invokerCreator = EasyMock.createMock(InvokerCreator.class);
//        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftQueryableProcessHandlerTest.class.getName()), SwiftQueryableProcessHandlerTest.class, null, false);
//        EasyMock.expect(invokerCreator.createAsyncInvoker(SwiftQueryableProcessHandlerTest.class, null)).andReturn(invoker).anyTimes();
//
//        PowerMock.mockStatic(SwiftContext.class);
//        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
//        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
//        EasyMock.expect(beanFactory.getBean(SwiftServiceInfoService.class)).andReturn(swiftServiceInfoService).anyTimes();
//        EasyMock.expect(swiftServiceInfoService.getServiceInfoByService(SwiftServiceInfoService.SERVICE)).andReturn(Collections.singletonList(swiftServiceInfoBean)).anyTimes();
//        EasyMock.expect(swiftServiceInfoBean.getServiceInfo()).andReturn("127.0.0.1:8080").anyTimes();
//
//        SegmentDestSelectRuleService rule = PowerMock.createMock(SegmentDestSelectRuleService.class);
//        EasyMock.expect(rule.getCurrentRule()).andReturn(null).anyTimes();
//        EasyMock.expect(beanFactory.getBean(SegmentDestSelectRuleService.class)).andReturn(rule).anyTimes();
//        PowerMock.replay(SwiftContext.class, rule);
//
//        URL url = EasyMock.createMock(URL.class);
//        UrlFactory urlFactory = EasyMock.createMock(UrlFactory.class);
//        UrlSelector.getInstance().switchFactory(urlFactory);
//        EasyMock.expect(urlFactory.getURL(EasyMock.anyObject())).andReturn(url).anyTimes();
//        EasyMock.replay(beanFactory, swiftServiceInfoBean, swiftServiceInfoService, invokerCreator, url, urlFactory);
//
//        PowerMock.mockStatic(SegmentLocationProvider.class);
//        provider = PowerMock.createMock(SegmentLocationProvider.class);
//        EasyMock.expect(SegmentLocationProvider.getInstance()).andReturn(provider).anyTimes();
//        PowerMock.replay(SegmentLocationProvider.class);
//    }
//
//    public void testProcessResult() throws Exception {
//        String tableName = "test";
//        QueryInfoBean bean = GroupQueryInfoBean.builder(tableName).build();
//        ((GroupQueryInfoBean) bean).setTableName(tableName);
//        String queryString = QueryBeanFactory.queryBean2String(bean);
//
//        EasyMock.expect(provider.getSegmentLocationURI(EasyMock.anyObject(SourceKey.class)))
//                .andReturn(new ArrayList<SegmentDestination>()).anyTimes();
//        PowerMock.replay(provider);
//
//        Query query = EasyMock.createMock(Query.class);
//        EasyMock.expect(query.getQueryResult()).andReturn(getLocalRS(queryString)).anyTimes();
//        PowerMock.mockStatic(QueryBuilder.class);
//        EasyMock.expect(QueryBuilder.buildPostQuery(EasyMock.anyObject(QueryResultSet.class), EasyMock.anyObject(QueryBean.class)))
//                .andReturn(query)
//                .anyTimes();
//        PowerMock.replay(query, QueryBuilder.class);
//
//        SwiftQueryableProcessHandler handler = new SwiftQueryableProcessHandler(invokerCreator);
//        try {
//            BaseSerializedQueryResultSet result = (BaseSerializedQueryResultSet) handler.processResult(
//                    SwiftQueryableProcessHandlerTest.class.getMethod("getLocalRS", String.class), new Target[]{Target.ANALYSE}, queryString);
//            assertNotNull(result);
//        } catch (Throwable throwable) {
//            assertTrue(false);
//        }
//    }
//
//    public BaseSerializedQueryResultSet getLocalRS(String quertString) {
//        BaseSerializedQueryResultSet rs = EasyMock.createMock(BaseSerializedQueryResultSet.class);
//        List<Map<Integer, Object>> map = new ArrayList<Map<Integer, Object>>();
//        EasyMock.expect(rs.getPage()).andReturn(Pair.of(new GroupNode(), map)).anyTimes();
//        rs.setInvoker(EasyMock.anyObject(BaseSerializedQueryResultSet.SyncInvoker.class));
//        EasyMock.expectLastCall().anyTimes();
//        QueryResultSetMerger merger = EasyMock.createMock(QueryResultSetMerger.class);
//        EasyMock.expect(merger.merge(EasyMock.anyObject(List.class))).andReturn(rs);
//        EasyMock.replay(rs, merger);
//        return rs;
//    }
//
//    public void testProcessUrl() {
//        SwiftQueryableProcessHandler handler = new SwiftQueryableProcessHandler(invokerCreator);
//        // 单机
//        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>();
//        List<Pair<URL, Set<String>>> pairs = handler.processUrl(new Target[]{Target.ANALYSE}, destinations);
//        assertEquals(1, pairs.size());
//        assertNull(pairs.get(0).getKey());
//        assertTrue(pairs.get(0).getValue().isEmpty());
//
//        destinations.add(new SegmentDestinationImpl("node1", "2", 2));
//        pairs = handler.processUrl(new Target[]{Target.ANALYSE}, destinations);
//        assertEquals(1, pairs.size());
//        assertNotNull(pairs.get(0).getKey());
//        assertEquals(1, pairs.get(0).getValue().size());
//    }
//}