package com.fr.swift.query;

import com.fr.swift.SwiftContext;
import com.fr.swift.basics.ProxyFactory;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.query.info.bean.query.GroupQueryInfoBean;
import com.fr.swift.query.info.bean.query.QueryBeanFactory;
import com.fr.swift.query.query.QueryBean;
import com.fr.swift.query.result.SwiftResultSetUtils;
import com.fr.swift.query.session.factory.SessionFactory;
import com.fr.swift.query.session.factory.SessionFactoryImpl;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.service.AnalyseService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by lyon on 2019/1/14.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class, ProxySelector.class, SwiftResultSetUtils.class})
public class QueryRunnerProviderTest {

    private AnalyseService service;

    @Before
    public void setUp() throws Exception {
        PowerMock.mockStatic(SwiftContext.class);
        BeanFactory beanFactory = EasyMock.createMock(BeanFactory.class);
        EasyMock.expect(SwiftContext.get()).andReturn(beanFactory).anyTimes();
        EasyMock.expect(beanFactory.getBean("swiftQuerySessionFactory", SessionFactory.class))
                .andReturn(new SessionFactoryImpl()).anyTimes();
        PowerMock.mockStatic(ProxySelector.class);
        ProxySelector selector = EasyMock.mock(ProxySelector.class);
        ProxyFactory factory = EasyMock.mock(ProxyFactory.class);
        service = Mockito.mock(AnalyseService.class);
        EasyMock.expect(factory.getProxy(AnalyseService.class)).andReturn(service).anyTimes();
        EasyMock.expect(selector.getFactory()).andReturn(factory).anyTimes();
        EasyMock.expect(ProxySelector.getInstance()).andReturn(selector).anyTimes();
        PowerMock.replay(SwiftContext.class, ProxySelector.class, beanFactory, factory, selector);
    }

    @Test
    public void query() throws Exception {
        QueryBean bean = new GroupQueryInfoBean();
        String json = QueryBeanFactory.queryBean2String(bean);
        QueryResultSet qrs = EasyMock.mock(QueryResultSet.class);
        SwiftResultSet resultSet = EasyMock.mock(SwiftResultSet.class);
        SwiftResultSet resultSet2 = EasyMock.mock(SwiftResultSet.class);
        when(service.getQueryResult(json)).thenReturn(qrs);
        PowerMock.mockStatic(SwiftResultSetUtils.class);
        EasyMock.expect(SwiftResultSetUtils.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet).times(1);
        EasyMock.expect(SwiftResultSetUtils.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet2).times(1);
        EasyMock.expect(SwiftResultSetUtils.toSwiftResultSet(EasyMock.anyObject(QueryResultSet.class),
                EasyMock.anyObject(QueryBean.class))).andReturn(resultSet).anyTimes();
        PowerMock.replay(SwiftResultSetUtils.class, qrs, resultSet);

        // 不缓存
        bean.setQueryId(null);
        json = QueryBeanFactory.queryBean2String(bean);
        when(service.getQueryResult(json)).thenReturn(qrs);
        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet2);

        bean.setQueryId(UUID.randomUUID().toString());
        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
        assertEquals(QueryRunnerProvider.getInstance().query(json), resultSet);
    }
}