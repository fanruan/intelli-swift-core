package com.fr.swift.config.convert.base;

import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.io.Serializable;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class AbstractSimpleConfigConvertTest {

    @Test
    public void testConstruct() {
        try {
            PowerMock.createMock(AbstractSimpleConfigConvert.class, Object.class);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void toBean() throws SQLException {
        AbstractSimpleConfigConvert<Boolean> mockAbstractSimpleConfigConvert = PowerMock.createMock(AbstractSimpleConfigConvert.class, Boolean.class);
        EasyMock.expect(mockAbstractSimpleConfigConvert.getNameSpace()).andReturn("nameSpace").anyTimes();
        SwiftConfigDao mockSwiftConfigDao = PowerMock.createMock(SwiftConfigDao.class);
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class), EasyMock.anyObject(Serializable.class))).andReturn(new SwiftConfigBean("nameSpace", "true")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class), EasyMock.anyObject(Serializable.class))).andReturn(null).once();
        SwiftConfigBean mockSwiftConfigEntity = PowerMock.createMock(SwiftConfigBean.class);
        EasyMock.expect(mockSwiftConfigEntity.getConfigValue()).andThrow(new RuntimeException()).anyTimes();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class), EasyMock.anyObject(Serializable.class))).andReturn(mockSwiftConfigEntity).once();
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        PowerMock.replayAll();
        assertTrue(mockAbstractSimpleConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession));
        boolean exception = false;
        try {
            mockAbstractSimpleConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            mockAbstractSimpleConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void toEntity() throws SQLException {
        AbstractSimpleConfigConvert<Boolean> mockAbstractSimpleConfigConvert = PowerMock.createMock(AbstractSimpleConfigConvert.class, Boolean.class);
        EasyMock.expect(mockAbstractSimpleConfigConvert.getNameSpace()).andReturn("nameSpace").anyTimes();
        PowerMock.replayAll();
        assertTrue(!mockAbstractSimpleConfigConvert.toEntity(true).isEmpty());
        PowerMock.verifyAll();
    }
}