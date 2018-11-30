package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.oper.ConfigCriteria;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.RestrictionFactory;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class SwiftServiceInfoDaoImplTest {

    @Test
    public void getServiceInfoByService() {
        RestrictionFactory mockRestrictionFactory = PowerMock.createMock(RestrictionFactory.class);
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("service"), EasyMock.notNull(String.class))).andReturn(new Object()).anyTimes();
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("service"), EasyMock.isNull(String.class))).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class, mockRestrictionFactory);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SwiftServiceInfoBean segmentKey = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.getServiceInfoByService(mockConfigSession, "clusterId").list().isEmpty());
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.getServiceInfoByService(mockConfigSession, null);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void getServiceInfoBySelective() {
        RestrictionFactory mockRestrictionFactory = PowerMock.createMock(RestrictionFactory.class);
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("clusterId"), EasyMock.notNull(String.class))).andReturn(new Object()).anyTimes();
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("service"), EasyMock.notNull(String.class))).andReturn(new Object()).anyTimes();
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("serviceInfo"), EasyMock.notNull(String.class))).andReturn(new Object()).anyTimes();
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class, mockRestrictionFactory);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SwiftServiceInfoBean segmentKey = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.getServiceInfoBySelective(mockConfigSession, segmentKey).list().isEmpty());
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.getServiceInfoBySelective(mockConfigSession, null);
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void deleteByServiceInfo() throws SQLException {
        RestrictionFactory mockRestrictionFactory = PowerMock.createMock(RestrictionFactory.class);
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("serviceInfo"), EasyMock.notNull(String.class))).andReturn(new Object()).anyTimes();
        EasyMock.expect(mockRestrictionFactory.eq(EasyMock.eq("serviceInfo"), EasyMock.isNull())).andThrow(new RuntimeException("Just Test Exception")).anyTimes();
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class, mockRestrictionFactory);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        ConfigCriteria mockConfigCriteria = PowerMock.createMock(ConfigCriteria.class);
        final SwiftServiceInfoBean segmentKey = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");

        EasyMock.expect(mockConfigCriteria.list()).andReturn(Arrays.asList(segmentKey.convert())).anyTimes();
        mockConfigCriteria.add(EasyMock.notNull());
        EasyMock.expectLastCall().anyTimes();
        EasyMock.expect(mockConfigSession.createCriteria(EasyMock.eq(SwiftServiceInfoBean.TYPE))).andReturn(mockConfigCriteria).anyTimes();
        mockConfigSession.delete(EasyMock.anyObject(SwiftServiceInfoBean.TYPE));
        EasyMock.expectLastCall().anyTimes();
        PowerMock.replayAll();
        assertTrue(mockSwiftMetaDataDaoImpl.deleteByServiceInfo(mockConfigSession, "serviceInfo"));
        boolean exception = false;
        try {
            mockSwiftMetaDataDaoImpl.deleteByServiceInfo(mockConfigSession, null);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }
}