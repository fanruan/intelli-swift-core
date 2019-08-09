package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftColumnIndexingConf;
import com.fr.swift.config.oper.ConfigSession;
import com.fr.swift.config.oper.ConfigSessionCreator;
import com.fr.swift.config.oper.impl.BaseConfigSessionCreator;
import com.fr.swift.config.service.IndexingConfService;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertNotNull;

/**
 * @author yee
 * @date 2019-01-04
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class SwiftColumnIndexingConfServiceTest extends BaseServiceTest {
    private IndexingConfService service;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);

        // Generate by Mock Plugin
        final ConfigSession mockConfigSession = mockSession(SwiftColumnIndexingConf.class);

        BaseConfigSessionCreator mockBaseTransactionManager = new BaseConfigSessionCreator() {
            @Override
            public ConfigSession createSession() {
                return mockConfigSession;
            }
        };
        EasyMock.expect(mockSwiftContext.getBean(EasyMock.eq(ConfigSessionCreator.class))).andReturn(mockBaseTransactionManager).anyTimes();
        PowerMock.replay(mockSwiftContext);
        service = new SwiftIndexingConfService();
    }

    @Test
    public void getColumnConf() {

        assertNotNull(service.getColumnConf(new SourceKey("sourceKey"), "column"));
// do test
        PowerMock.verifyAll();

    }

    @Test
    public void setColumnConf() {
        // Generate by Mock Plugin
        SwiftColumnIndexingConf bean = new SwiftColumnIndexingConf(new SourceKey("sourceKey"), "column", true, true);
        service.setColumnConf(bean);
// do test
        PowerMock.verifyAll();

    }
}