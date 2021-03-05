package com.fr.swift.cloud.segment;

import com.fr.swift.cloud.SwiftContext;
import com.fr.swift.cloud.config.entity.SwiftMetaDataEntity;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucket;
import com.fr.swift.cloud.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.cloud.config.entity.SwiftSegmentEntity;
import com.fr.swift.cloud.config.service.SwiftMetaDataService;
import com.fr.swift.cloud.config.service.SwiftSegmentService;
import com.fr.swift.cloud.cube.io.Types;
import com.fr.swift.cloud.db.SwiftDatabase;
import com.fr.swift.cloud.source.SourceKey;
import com.fr.swift.cloud.source.SwiftMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author lucifer
 * @date 2020/3/18
 * @description
 * @since swift 1.1
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@PrepareForTest({SwiftContext.class, SegmentUtils.class})
public class SegmentContainerTest {

    @Mock
    SwiftContext swiftContext;
    @Mock
    SwiftMetaDataService metaDataService;
    @Mock
    SwiftSegmentService swiftSegmentService;

    @Mock
    Segment segment;

    SegmentKey a0 = new SwiftSegmentEntity(new SourceKey("testA"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey a1 = new SwiftSegmentEntity(new SourceKey("testA"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey a2 = new SwiftSegmentEntity(new SourceKey("testA"), 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    SegmentKey b0 = new SwiftSegmentEntity(new SourceKey("testB"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey b1 = new SwiftSegmentEntity(new SourceKey("testB"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey b2 = new SwiftSegmentEntity(new SourceKey("testB"), 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    SegmentKey c0 = new SwiftSegmentEntity(new SourceKey("testC"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey c1 = new SwiftSegmentEntity(new SourceKey("testC"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey c2 = new SwiftSegmentEntity(new SourceKey("testC"), 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    SwiftMetaData d = new SwiftMetaDataEntity.Builder().setTableName("testD").setSwiftSchema(SwiftDatabase.CUBE).setFields(Collections.singletonList(null)).build();
    SegmentKey d0 = new SwiftSegmentEntity(new SourceKey("testD"), 0, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey d1 = new SwiftSegmentEntity(new SourceKey("testD"), 1, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);
    SegmentKey d2 = new SwiftSegmentEntity(new SourceKey("testD"), 2, Types.StoreType.FINE_IO, SwiftDatabase.CUBE);

    @Before
    public void setUp() throws Exception {
        mockStatic(SwiftContext.class, SegmentUtils.class);
        when(SwiftContext.get()).thenReturn(swiftContext);
        when(swiftContext.getBean(SwiftMetaDataService.class)).thenReturn(metaDataService);
        when(swiftContext.getBean(SwiftSegmentService.class)).thenReturn(swiftSegmentService);

        when(metaDataService.getAllMetas()).thenReturn(Stream.of(
                new SwiftMetaDataEntity.Builder().setTableName("testA").setSwiftSchema(SwiftDatabase.CUBE).setFields(Collections.singletonList(null)).build()
                , new SwiftMetaDataEntity.Builder().setTableName("testB").setSwiftSchema(SwiftDatabase.CUBE).setFields(Collections.singletonList(null)).build()
                , new SwiftMetaDataEntity.Builder().setTableName("testC").setSwiftSchema(SwiftDatabase.CUBE).setFields(Collections.singletonList(null)).build())
                .collect(Collectors.toList()));
        when(swiftSegmentService.getOwnSegments(new SourceKey("testA"))).thenReturn(Stream.of(a0, a1).collect(Collectors.toList()));
        when(swiftSegmentService.getOwnSegments(new SourceKey("testB"))).thenReturn(Stream.of(b0, b1).collect(Collectors.toList()));
        when(swiftSegmentService.getOwnSegments(new SourceKey("testC"))).thenReturn(Stream.of(c0, c1).collect(Collectors.toList()));

        when(swiftSegmentService.getBucketByTable(new SourceKey("testA"))).thenReturn(new SwiftSegmentBucket(new SourceKey("testA")));
        when(swiftSegmentService.getBucketByTable(new SourceKey("testB"))).thenReturn(new SwiftSegmentBucket(new SourceKey("testC")));
        when(swiftSegmentService.getBucketByTable(new SourceKey("testC"))).thenReturn(new SwiftSegmentBucket(new SourceKey("testC")));

        doReturn(segment).when(SegmentUtils.class, "newSegment", Mockito.any(SegmentKey.class));
    }

    @Test
    public void testSegment() {
        assertTrue(SegmentContainer.LOCAL.exist(a0));
        assertTrue(SegmentContainer.LOCAL.exist(a1));
        assertFalse(SegmentContainer.LOCAL.exist(a2));
        SegmentContainer.LOCAL.addSegment(a2);
        assertTrue(SegmentContainer.LOCAL.exist(a2));
        assertEquals(segment, SegmentContainer.LOCAL.getSegment(a2));
        assertEquals(3, SegmentContainer.LOCAL.getSegments(new SourceKey("testA")).size());
        assertEquals(2, SegmentContainer.LOCAL.getSegments(Stream.of("testB@FINE_IO@0", "testB@FINE_IO@1", "testB@FINE_IO@2").collect(Collectors.toSet())).size());
        assertEquals(3, SegmentContainer.LOCAL.getSegments(new SourceKey("testA")).size());
        assertEquals(3, SegmentContainer.LOCAL.getSegmentKeys(new SourceKey("testA")).size());

        SegmentContainer.LOCAL.addSegment(d0);
        SegmentContainer.LOCAL.addSegment(d1);
        assertTrue(SegmentContainer.LOCAL.exist(d0));
        assertTrue(SegmentContainer.LOCAL.exist(d1));
        assertFalse(SegmentContainer.LOCAL.exist(d2));

        assertEquals(a2, SegmentContainer.LOCAL.removeSegment(a2));
        assertFalse(SegmentContainer.LOCAL.exist(a2));

        assertEquals(2, SegmentContainer.LOCAL.removeSegments(Stream.of(d0, d1, d2).collect(Collectors.toList())).size());
        assertFalse(SegmentContainer.LOCAL.exist(d0));
        assertFalse(SegmentContainer.LOCAL.exist(d1));
        assertFalse(SegmentContainer.LOCAL.exist(d2));

        assertNotNull(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testD")));

        assertTrue(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketMap().isEmpty());
        assertTrue(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketIndexMap().isEmpty());
        SegmentContainer.LOCAL.saveBucket(new SwiftSegmentBucketElement("testA", 0, "testA@FINE_IO@0"));
        assertEquals(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketMap().get(0).size(), 1);
        assertEquals(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketIndexMap().size(), 1);
        assertEquals(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketMap().get(0).get(0).getId(), "testA@FINE_IO@0");

        SegmentContainer.LOCAL.deleteBucket(a0);
        assertTrue(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketMap().get(0).isEmpty());
        assertTrue(SegmentContainer.LOCAL.getBucketByTable(new SourceKey("testA")).getBucketIndexMap().isEmpty());
    }
}