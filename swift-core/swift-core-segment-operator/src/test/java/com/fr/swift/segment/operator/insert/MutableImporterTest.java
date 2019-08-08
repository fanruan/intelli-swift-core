package com.fr.swift.segment.operator.insert;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.factory.BeanFactory;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.config.service.SwiftTableAllotRuleService;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.cube.CubePathBuilder;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Database;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.result.SwiftMutableResultSet;
import com.fr.swift.result.SwiftMutableResultSetTest;
import com.fr.swift.segment.MutableCacheColumnSegment;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.Row;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.alloter.RowInfo;
import com.fr.swift.source.alloter.SwiftSourceAlloter;
import com.fr.swift.source.alloter.impl.SwiftSegmentInfo;
import com.fr.swift.source.alloter.impl.line.LineAllotRule;
import com.fr.swift.source.split.json.JsonColumnSplitRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author lucifer
 * @date 2019-08-01
 * @description
 * @since advanced swift 1.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(MockitoJUnitRunner.class)
@SuppressStaticInitializationFor("com.fr.swift.db.impl.SwiftDatabase")
@PrepareForTest({SwiftContext.class, MutableImporter.class, CubeUtil.class, MutableInserter.class, SwiftDatabase.class})
public class MutableImporterTest {

    SwiftMetaData baseMetadata = SwiftMutableResultSetTest.getFirstMetadata();

    @Mock
    DataSource dataSource;

    @Mock
    SwiftSourceAlloter alloter;

    @Mock
    SwiftTableAllotRuleService allotRuleService;

    @Mock
    MutableInserter mutableInserter;

    @Mock
    Database database;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(SwiftContext.class);
        PowerMockito.mockStatic(SwiftDatabase.class);

        Mockito.when(SwiftContext.get()).thenReturn(Mockito.mock(BeanFactory.class));
        Mockito.when(SwiftDatabase.getInstance()).thenReturn(database);

        Mockito.when(dataSource.getMetadata()).thenReturn(baseMetadata);
        Mockito.when(dataSource.getSourceKey()).thenReturn(new SourceKey("test"));
        final ContextProvider mock = mock(ContextProvider.class);
        when(mock.getContextPath()).thenReturn("/");
        when(SwiftContext.get().getBean(ContextProvider.class)).thenReturn(mock);

        SwiftConfig service = mock(SwiftConfig.class);
        when(SwiftContext.get().getBean(SwiftConfig.class)).thenReturn(service);
        final SwiftConfigEntityQueryBus query = mock(SwiftConfigEntityQueryBus.class);
        when(service.query(SwiftConfigEntity.class)).thenReturn(query);
        when(query.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, "/")).thenReturn("/");
        PowerMockito.mockStatic(CubeUtil.class);
        Mockito.when(CubeUtil.getCurrentDir(new SourceKey("test"))).thenReturn(1);
        CubePathBuilder cubePathBuilder = mock(CubePathBuilder.class, Mockito.RETURNS_DEEP_STUBS);
        PowerMockito.whenNew(CubePathBuilder.class).withArguments(Mockito.any(SegmentKey.class)).thenReturn(cubePathBuilder);
        ResourceLocation location = mock(ResourceLocation.class);
        PowerMockito.whenNew(ResourceLocation.class).withArguments(
                cubePathBuilder.setTempDir(CubeUtil.getCurrentDir(new SourceKey("test"))).build(),
                Types.StoreType.FINE_IO).thenReturn(location);

        PowerMockito.whenNew(MutableCacheColumnSegment.class).withAnyArguments().thenReturn(Mockito.mock(MutableCacheColumnSegment.class));
        PowerMockito.whenNew(MutableInserter.class).withAnyArguments().thenReturn(mutableInserter);
        Mockito.when(SwiftContext.get().getBean(SwiftTableAllotRuleService.class)).thenReturn(allotRuleService);

        Mockito.when(alloter.getAllotRule()).thenReturn(new LineAllotRule());
        Mockito.when(alloter.allot(Mockito.any(RowInfo.class))).thenReturn(new SwiftSegmentInfo(0, Types.StoreType.FINE_IO));
    }

    @Test
    public void integrationImport() throws Exception {
        SwiftMutableResultSet swiftMutableResultSet = new SwiftMutableResultSet(baseMetadata
                , new SwiftMutableResultSetTest.MutableTestResultSet(baseMetadata, SwiftMutableResultSetTest.lines1)
                , new JsonColumnSplitRule("body", baseMetadata));
        MutableImporter mutableImporter = new MutableImporter(dataSource, alloter);
        mutableImporter.importData(swiftMutableResultSet);

        Mockito.verify(mutableInserter, Mockito.times(3)).refreshMetadata(Mockito.any(SwiftMetaData.class));
        Mockito.verify(mutableInserter, Mockito.times(5)).insertData(Mockito.any(Row.class));

    }
}