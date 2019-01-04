package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigWhere;
import org.easymock.EasyMock;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SwiftServiceInfoDaoImplTest {
    private SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = new SwiftServiceInfoDaoImpl();

    @Test
    public void getServiceInfoByService() {
        // Generate by Mock Plugin
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        // Generate by Mock Plugin
        SwiftServiceInfoBean bean = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(bean.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.getServiceInfoByService(mockConfigSession, EasyMock.notNull(String.class)).isEmpty());
        // do test
        PowerMock.verifyAll();

    }

    @Test
    public void getServiceInfoBySelective() {
        final SwiftServiceInfoBean bean = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class), EasyMock.notNull(ConfigWhere.class), EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(bean.convert())).anyTimes();
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigQuery).anyTimes();
        PowerMock.replayAll();

        assertFalse(mockSwiftMetaDataDaoImpl.getServiceInfoBySelective(mockConfigSession, bean).list().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void deleteByServiceInfo() throws SQLException {
        SwiftServiceInfoBean bean = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        // Generate by Mock Plugin
        ConfigQuery mockConfigQuery = PowerMock.createMock(ConfigQuery.class);
        mockConfigQuery.where(EasyMock.notNull(ConfigWhere.class));
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigQuery.executeQuery()).andReturn(Arrays.asList(bean.convert()));
        EasyMock.expect(mockConfigSession.createEntityQuery(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigQuery);
        mockConfigSession.delete(EasyMock.anyObject(SwiftServiceInfoBean.TYPE));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(mockSwiftMetaDataDaoImpl.deleteByServiceInfo(mockConfigSession, "serviceInfo"));
        PowerMock.verifyAll();
    }
}