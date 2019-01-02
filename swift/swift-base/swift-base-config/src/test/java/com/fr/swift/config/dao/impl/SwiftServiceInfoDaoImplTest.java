package com.fr.swift.config.dao.impl;

import com.fr.swift.config.bean.SwiftServiceInfoBean;
import com.fr.swift.config.dao.SwiftServiceInfoDao;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.converter.FindListImpl;
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
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockSwiftMetaDataDaoImpl.getServiceInfoByService(EasyMock.eq(mockConfigSession), EasyMock.eq("clusterId")))
                .andReturn(new FindListImpl<SwiftServiceInfoBean>(Arrays.asList(EasyMock.anyObject()))).once();
        EasyMock.expect(mockSwiftMetaDataDaoImpl.getServiceInfoByService(EasyMock.eq(mockConfigSession), null))
                .andThrow(new RuntimeException()).once();
        PowerMock.replayAll();
        assertFalse(mockSwiftMetaDataDaoImpl.getServiceInfoByService(mockConfigSession, "clusterId").isEmpty());
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
        final SwiftServiceInfoBean segmentKey = new SwiftServiceInfoBean("service", "clusterId", "serviceInfo");
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockSwiftMetaDataDaoImpl.getServiceInfoBySelective(EasyMock.eq(mockConfigSession), EasyMock.notNull(SwiftServiceInfoBean.class)))
                .andReturn(new FindListImpl<SwiftServiceInfoBean>(Arrays.asList(segmentKey))).once();
        EasyMock.expect(mockSwiftMetaDataDaoImpl.getServiceInfoBySelective(EasyMock.eq(mockConfigSession), EasyMock.isNull(SwiftServiceInfoBean.class)))
                .andThrow(new RuntimeException()).once();
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
        SwiftServiceInfoDao mockSwiftMetaDataDaoImpl = PowerMock.createMock(SwiftServiceInfoDaoImpl.class);
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        EasyMock.expect(mockSwiftMetaDataDaoImpl.deleteByServiceInfo(EasyMock.eq(mockConfigSession), EasyMock.eq("serviceInfo"))).andReturn(true).once();
        EasyMock.expect(mockSwiftMetaDataDaoImpl.deleteByServiceInfo(EasyMock.eq(mockConfigSession), EasyMock.isNull(String.class))).andThrow(new SQLException()).once();
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