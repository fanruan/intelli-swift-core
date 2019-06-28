package com.fr.swift.config.dao.impl;

import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.oper.ConfigQuery;
import com.fr.swift.config.oper.ConfigSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({})
public class SwiftSegmentBucketDaoTest {
    @Mock
    ConfigSession session;

    String sourceKey = "test";

    String segmentKey = "testSegmentkey";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void deleteBySourceKey() throws SQLException {
        ConfigQuery configQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(session.createEntityQuery(Mockito.any(Class.class))).thenReturn(configQuery);
        SwiftSegmentBucketElement element = Mockito.mock(SwiftSegmentBucketElement.class);
        Mockito.when(configQuery.executeQuery()).thenReturn(Arrays.asList(element));
        new SwiftSegmentBucketDaoImpl().deleteBySourceKey(session, sourceKey);
        Mockito.verify(session).delete(element);
    }

    @Test
    public void deleteBySegmentKey() throws SQLException {

        ConfigQuery configQuery = Mockito.mock(ConfigQuery.class);
        Mockito.when(session.createEntityQuery(Mockito.any(Class.class))).thenReturn(configQuery);
        SwiftSegmentBucketElement element = Mockito.mock(SwiftSegmentBucketElement.class);
        Mockito.when(configQuery.executeQuery()).thenReturn(Arrays.asList(element));
        new SwiftSegmentBucketDaoImpl().deleteBySegmentKey(session, segmentKey);
        Mockito.verify(session).delete(element);

    }
}
