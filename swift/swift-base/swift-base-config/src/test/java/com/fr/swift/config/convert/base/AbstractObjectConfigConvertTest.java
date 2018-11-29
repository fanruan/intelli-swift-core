package com.fr.swift.config.convert.base;

import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author yee
 * @date 2018-11-29
 */
public class AbstractObjectConfigConvertTest {

    @Test
    public void toBean() throws SQLException {
        AbstractObjectConfigConvert<TestObject> mockAbstractObjectConfigConvert = PowerMock.createMock(AbstractObjectConfigConvert.class, TestObject.class);
        EasyMock.expect(mockAbstractObjectConfigConvert.getNameSpace()).andReturn("nameSpace").anyTimes();
        EasyMock.expect(mockAbstractObjectConfigConvert.transferClassName(EasyMock.eq("com.fr.swift.config.convert.base.TestObject"))).andReturn("com.fr.swift.config.convert.base.TestObject").anyTimes();
        EasyMock.expect(mockAbstractObjectConfigConvert.transferClassName(EasyMock.anyString())).andReturn("com.fr.swift.config.convert.base.AbstractObjectConfigConvertTest").anyTimes();
        SwiftConfigDao mockSwiftConfigDao = PowerMock.createMock(SwiftConfigDao.class);
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.class"))).andReturn(null).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.class"))).andReturn(new SwiftConfigBean("nameSpace.class", "com.fr.swift.config.convert.base.AbstractObjectConfigConvertTest")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.class"))).andReturn(new SwiftConfigBean("nameSpace.class", "com.fr.swift.config.convert.base.TestObject")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.name"))).andReturn(new SwiftConfigBean("nameSpace.name", "小明")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.age"))).andReturn(new SwiftConfigBean("nameSpace.age", "20")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.class"))).andReturn(new SwiftConfigBean("nameSpace.class", "com.fr.swift.config.convert.base.TestObject")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.name"))).andReturn(new SwiftConfigBean("nameSpace.name", "小明")).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq("nameSpace.age"))).andReturn(new SwiftConfigBean("nameSpace.age", "exception")).once();
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        PowerMock.replayAll();
        boolean exception = false;
        try {
            mockAbstractObjectConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try {
            mockAbstractObjectConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        TestObject testObject = mockAbstractObjectConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        assertEquals("小明", testObject.getName());
        assertEquals(20, testObject.getAge());
        exception = false;
        try {
            mockAbstractObjectConfigConvert.toBean(mockSwiftConfigDao, mockConfigSession);
        } catch (SQLException e) {
            exception = true;
        }
        assertTrue(exception);
        PowerMock.verifyAll();
    }

    @Test
    public void toEntity() {
        AbstractObjectConfigConvert<TestObject> mockAbstractObjectConfigConvert = PowerMock.createMock(AbstractObjectConfigConvert.class, TestObject.class);
        EasyMock.expect(mockAbstractObjectConfigConvert.getNameSpace()).andReturn("nameSpace").anyTimes();
        EasyMock.expect(mockAbstractObjectConfigConvert.transferClassName(EasyMock.eq("com.fr.swift.config.convert.base.TestObject"))).andReturn("com.fr.swift.config.convert.base.TestObject").anyTimes();
        PowerMock.replayAll();
        TestObject testObject = new TestObject();
        testObject.setAge(100);
        testObject.setName("Mark");
        List<SwiftConfigBean> list = mockAbstractObjectConfigConvert.toEntity(testObject);
        assertEquals(3, list.size());
        assertEquals("nameSpace.class", list.get(0).getConfigKey());
        assertEquals("com.fr.swift.config.convert.base.TestObject", list.get(0).getConfigValue());
        assertEquals("Mark", list.get(1).getConfigValue());
        assertEquals("nameSpace.name", list.get(1).getConfigKey());
        assertEquals("100", list.get(2).getConfigValue());
        assertEquals("nameSpace.age", list.get(2).getConfigKey());
        PowerMock.verifyAll();
    }
}