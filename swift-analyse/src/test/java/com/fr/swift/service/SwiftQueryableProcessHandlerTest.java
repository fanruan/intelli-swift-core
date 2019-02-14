package com.fr.swift.service;

import com.fr.swift.SwiftContext;
import com.fr.swift.basic.URL;
import com.fr.swift.basics.Invoker;
import com.fr.swift.basics.InvokerCreator;
import com.fr.swift.basics.UrlFactory;
import com.fr.swift.basics.annotation.ProxyService;
import com.fr.swift.basics.annotation.Target;
import com.fr.swift.basics.base.ProxyServiceRegistry;
import com.fr.swift.basics.base.selector.UrlSelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.service.SegmentDestSelectRuleService;
import com.fr.swift.config.service.SwiftServiceInfoService;
import com.fr.swift.local.LocalInvoker;
import com.fr.swift.query.builder.QueryBuilder;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.info.bean.query.QueryInfoBean;
import com.fr.swift.query.query.Query;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.serialize.BaseSerializableQRS;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.resultset.INodeQueryResultSetMerger;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.SegmentDestination;
import com.fr.swift.segment.SegmentLocationProvider;
import com.fr.swift.segment.impl.SegmentDestinationImpl;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.Pair;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyon on 2019/1/7.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, SegmentLocationProvider.class, QueryBuilder.class})
@ProxyService(SwiftQueryableProcessHandlerTest.class)
public class SwiftQueryableProcessHandlerTest extends TestCase {

    private InvokerCreator invokerCreator;
    private SegmentLocationProvider provider;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ProxyServiceRegistry.get().registerService(new SwiftQueryableProcessHandlerTest());

        SwiftServiceInfoBean swiftServiceInfoBean = EasyMock.createMock(SwiftServiceInfoBean.class);
        SwiftServiceInfoService swiftServiceInfoService = EasyMock.createMock(SwiftServiceInfoService.class);
        invokerCreator = EasyMock.createMock(InvokerCreator.class);
        Invoker invoker = new LocalInvoker(ProxyServiceRegistry.get().getService(SwiftQueryableProcessHandlerTest.class.getName()), SwiftQueryableProcessHandlerTest.class, null, false);
        EasyMock.expect(invokerCreator.createAsyncInvoker(SwiftQueryableProcessHandlerTest.class, null)).andReturn(invoker).anyTimes();

        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        EasyMock.expect(beanFactory.getBean(SwiftServiceInfoService.class)).andReturn(swiftServiceInfoService).anyTimes();
        EasyMock.expect(swiftServiceInfoService.getServiceInfoByService(SwiftServiceInfoService.SERVICE)).andReturn(Collections.singletonList(swiftServiceInfoBean)).anyTimes();
        EasyMock.expect(swiftServiceInfoBean.getServiceInfo()).andReturn("127.0.0.1:8080").anyTimes();

        SegmentDestSelectRuleService rule = PowerMock.createMock(SegmentDestSelectRuleService.class);
        EasyMock.expect(rule.getCurrentRule()).andReturn(null).anyTimes();
        EasyMock.expect(beanFactory.getBean(SegmentDestSelectRuleService.class)).andReturn(rule).anyTimes();
        PowerMock.replay(SwiftContext.class, rule);

        URL url = EasyMock.createMock(URL.class);
        UrlFactory urlFactory = EasyMock.createMock(UrlFactory.class);
        UrlSelector.getInstance().switchFactory(urlFactory);
        EasyMock.expect(urlFactory.getURL(EasyMock.anyObject())).andReturn(url).anyTimes();
        EasyMock.replay(beanFactory, swiftServiceInfoBean, swiftServiceInfoService, invokerCreator, url, urlFactory);

        PowerMock.mockStatic(SegmentLocationProvider.class);
        provider = PowerMock.createMock(SegmentLocationProvider.class);
        EasyMock.expect(SegmentLocationProvider.getInstance()).andReturn(provider).anyTimes();
        PowerMock.replay(SegmentLocationProvider.class);
    }

    public void testProcessResult() throws Exception {
        String tableName = "test";
        QueryInfoBean bean = new GroupQueryInfoBean();
        ((GroupQueryInfoBean) bean).setTableName(tableName);
        String queryString = QueryBeanFactory.queryBean2String(bean);

        EasyMock.expect(provider.getSegmentLocationURI(EasyMock.anyObject(SourceKey.class)))
                .andReturn(new ArrayList<SegmentDestination>()).anyTimes();
        PowerMock.replay(provider);

        Query query = EasyMock.createMock(Query.class);
        EasyMock.expect(query.getQueryResult()).andReturn(getLocalRS(queryString)).anyTimes();
        PowerMock.mockStatic(QueryBuilder.class);
        EasyMock.expect(QueryBuilder.buildPostQuery(EasyMock.anyObject(QueryResultSet.class), EasyMock.anyObject(QueryBean.class)))
                .andReturn(query)
                .anyTimes();
        PowerMock.replay(query, QueryBuilder.class);

        SwiftQueryableProcessHandler handler = new SwiftQueryableProcessHandler(invokerCreator);
        try {
            BaseSerializableQRS result = (BaseSerializableQRS) handler.processResult(
                    SwiftQueryableProcessHandlerTest.class.getMethod("getLocalRS", String.class), new Target[]{Target.ANALYSE}, queryString);
            assertNotNull(result);
        } catch (Throwable throwable) {
            assertTrue(false);
        }
    }

    public BaseSerializableQRS getLocalRS(String quertString) {
        BaseSerializableQRS rs = EasyMock.createMock(BaseSerializableQRS.class);
        List<Map<Integer, Object>> map = new ArrayList<Map<Integer, Object>>();
        EasyMock.expect(rs.getPage()).andReturn(Pair.of(new GroupNode(), map)).anyTimes();
        rs.setInvoker(EasyMock.anyObject(BaseSerializableQRS.SyncInvoker.class));
        EasyMock.expectLastCall().anyTimes();
        INodeQueryResultSetMerger merger = EasyMock.createMock(INodeQueryResultSetMerger.class);
        EasyMock.expect(merger.merge(EasyMock.anyObject(List.class))).andReturn(rs);
        EasyMock.expect(rs.getMerger()).andReturn(merger);
        EasyMock.replay(rs, merger);
        return rs;
    }

    public void testProcessUrl() {
        SwiftQueryableProcessHandler handler = new SwiftQueryableProcessHandler(invokerCreator);
        // 单机
        List<SegmentDestination> destinations = new ArrayList<SegmentDestination>();
        List<Pair<URL, Set<String>>> pairs = handler.processUrl(new Target[]{Target.ANALYSE}, destinations);
        assertEquals(1, pairs.size());
        assertNull(pairs.get(0).getKey());
        assertTrue(pairs.get(0).getValue().isEmpty());

        destinations.add(new SegmentDestinationImpl("node1", "2", 2));
        pairs = handler.processUrl(new Target[]{Target.ANALYSE}, destinations);
        assertEquals(1, pairs.size());
        assertNotNull(pairs.get(0).getKey());
        assertEquals(1, pairs.get(0).getValue().size());
    }
}