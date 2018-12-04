package com.fr.swift.config.convert;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.DataSyncRule;
import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.config.dao.SwiftConfigDao;
import com.fr.swift.config.oper.ConfigSession;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author yee
 * @date 2018-11-29
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class DataSyncRuleConvertTest {
    private DataSyncRuleConvert convert = new DataSyncRuleConvert();

    @Test
    public void toBean() throws SQLException {
        SwiftConfigDao mockSwiftConfigDao = PowerMock.createMock(SwiftConfigDao.class);
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq(convert.getNameSpace() + ".class"))).andReturn(new SwiftConfigBean(convert.getNameSpace() + ".class", TestDataSyncRule.class.getName())).once();
        EasyMock.expect(mockSwiftConfigDao.select(EasyMock.anyObject(ConfigSession.class),
                EasyMock.eq(convert.getNameSpace() + ".class"))).andReturn(new SwiftConfigBean(convert.getNameSpace() + ".class", DataSyncRuleConvertTest.class.getName())).once();
        ConfigSession mockConfigSession = PowerMock.createMock(ConfigSession.class);
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        EasyMock.expect(SwiftContext.get().getBean(EasyMock.eq("defaultDataSyncRule"), EasyMock.eq(DataSyncRule.class))).andReturn(new TestDataSyncRule("defaultDataSyncRule"));
        PowerMock.replayAll();
        TestDataSyncRule rule = (TestDataSyncRule) convert.toBean(mockSwiftConfigDao, mockConfigSession);
        assertNull(rule.toString());
        TestDataSyncRule rule1 = (TestDataSyncRule) convert.toBean(mockSwiftConfigDao, mockConfigSession);
        assertEquals(rule1.toString(), "defaultDataSyncRule");
        PowerMock.verifyAll();
    }
}