package com.fr.swift.config.convert;

import com.fr.swift.config.annotation.ConfigField;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.lang.reflect.Field;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author yee
 * @date 2018-11-29
 */
public class SwiftFileSystemConvertTest {
    private SwiftFileSystemConvert convert = new SwiftFileSystemConvert();

    @Test
    public void toBean() throws SQLException, ClassNotFoundException {
        SwiftConfigDao mockSwiftConfigDao = PowerMock.createMock(SwiftConfigDao.class);
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq(convert.getNameSpace() + ".class"))).andReturn(new SwiftConfigBean(convert.getNameSpace() + ".class", TestSwiftFileSystemConfig.class.getName())).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq(convert.getNameSpace() + ".class"))).andReturn(new SwiftConfigBean(convert.getNameSpace() + ".class", "com.fr.swift.config.bean.FtpRepositoryConfigBean")).once();
        for (Field field : this.getClass().getClassLoader().loadClass("com.fr.swift.repository.config.FtpRepositoryConfig").getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigField.class)) {
                EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class), EasyMock.eq(convert.getNameSpace() + "." + field.getName()))).andReturn(new SwiftConfigBean("", "0"));
            }
        }
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq(convert.getNameSpace() + ".class"))).andReturn(new SwiftConfigBean(convert.getNameSpace() + ".class", "com.fr.swift.config.bean.HdfsRepositoryConfigBean")).once();
        for (Field field : this.getClass().getClassLoader().loadClass("com.fr.swift.repository.config.HdfsRepositoryConfig").getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigField.class)) {
                EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class), EasyMock.eq(convert.getNameSpace() + "." + field.getName()))).andReturn(new SwiftConfigBean("", "0"));
            }
        }
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        PowerMock.replayAll();
        TestSwiftFileSystemConfig rule = (TestSwiftFileSystemConfig) convert.toBean(mockSwiftConfigDao, mockConfigSession);
        assertNotNull(rule);
        assertEquals("FTP", convert.toBean(mockSwiftConfigDao, mockConfigSession).getType().name());
        assertEquals("HDFS", convert.toBean(mockSwiftConfigDao, mockConfigSession).getType().name());
        PowerMock.verifyAll();
    }
}