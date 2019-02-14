package com.fr.swift.jdbc.sql;

import com.fr.swift.SwiftContext;
import com.fr.swift.api.server.ApiServerService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-09
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SwiftContext.class)
public class EmbSwiftConnectionTest {
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        Class.forName("com.fr.swift.jdbc.sql.NoopDriver");
        connection = DriverManager.getConnection("jdbc:swift:test:emb://default");
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext mockSwiftContext = PowerMock.createMock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(mockSwiftContext).anyTimes();
        PowerMock.replay(SwiftContext.class);
        ApiServerService mockApiServerService = PowerMock.createMock(ApiServerService.class);
        EasyMock.expect(mockApiServerService.dispatchRequest(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(mockSwiftContext.getBean(ApiServerService.class)).andReturn(mockApiServerService).once();
        EasyMock.expect(mockSwiftContext.getBean(ApiServerService.class)).andReturn(new TestApiServerService()).once();
        PowerMock.replay(mockApiServerService, mockSwiftContext);
    }

    @Test
    public void jdbcTest() throws SQLException {
        assertTrue(connection instanceof EmbSwiftConnection);
        Statement statement = connection.createStatement();
        boolean exception = false;
        try {
            statement.executeQuery("select * from tableA");
        } catch (Exception e) {
            exception = true;
        }
        assertTrue(exception);
        assertNotNull(statement.executeQuery("select * from tableA"));
        PowerMock.verifyAll();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }
}