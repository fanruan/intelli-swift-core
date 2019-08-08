package com.fr.swift.cube;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.SwiftConfig;
import com.fr.swift.config.SwiftConfigConstants;
import com.fr.swift.config.entity.SwiftConfigEntity;
import com.fr.swift.config.query.SwiftConfigEntityQueryBus;
import com.fr.swift.context.ContextProvider;
import com.fr.swift.db.SwiftSchema;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author anchore
 * @date 12/7/2018
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SwiftContext.class})
public class CubePathBuilderTest {

    private String basePath;

    @Before
    public void setUp() throws Exception {
        PowerMock.mockStatic(SwiftContext.class);
        SwiftContext swiftContext = EasyMock.mock(SwiftContext.class);
        EasyMock.expect(SwiftContext.get()).andReturn(swiftContext).anyTimes();
        PowerMock.replayAll();

        basePath = "/base_path";
        final ContextProvider contextProvider = EasyMock.createMock(ContextProvider.class);
        EasyMock.expect(contextProvider.getContextPath()).andReturn(basePath).anyTimes();
        EasyMock.expect(swiftContext.getBean(ContextProvider.class)).andReturn(contextProvider).anyTimes();

        final SwiftConfig mock = EasyMock.createMock(SwiftConfig.class);
        final SwiftConfigEntityQueryBus mock1 = EasyMock.createMock(SwiftConfigEntityQueryBus.class);

        EasyMock.expect(mock.query(SwiftConfigEntity.class)).andReturn(mock1).anyTimes();
        EasyMock.expect(mock1.select(SwiftConfigConstants.Namespace.SWIFT_CUBE_PATH, String.class, basePath)).andReturn(basePath).anyTimes();

        EasyMock.replay(contextProvider, mock, mock1, swiftContext);
    }

    @Test(expected = RuntimeException.class)
    public void testWrongBuild() {
        new CubePathBuilder().build();
    }

    @Test(expected = RuntimeException.class)
    public void testWrongBuild1() {
        new CubePathBuilder().setSwiftSchema(SwiftSchema.CUBE).setTempDir(1).asBackup().build();
    }

    @Test
    public void testBuild() {
        SourceKey tableKey = new SourceKey("table1");
        SwiftSchema swiftSchema = SwiftSchema.CUBE;
        String columnId = "column1";
        int segOrder = 10;

        String path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTempDir(0)
                .setTableKey(tableKey)
                .setSegOrder(segOrder)
                .setColumnId(columnId).build();
        Assert.assertEquals(String.format("%s/0/%s/seg%d/%s", swiftSchema.getDir(), tableKey, segOrder, columnId), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTableKey(tableKey)
                .setSegOrder(segOrder)
                .setColumnId(columnId).build();
        Assert.assertEquals(String.format("%s/%s/seg%d/%s", swiftSchema.getDir(), tableKey, segOrder, columnId), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTableKey(tableKey)
                .setSegOrder(segOrder).build();
        Assert.assertEquals(String.format("%s/%s/seg%d", swiftSchema.getDir(), tableKey, segOrder), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTableKey(tableKey).build();
        Assert.assertEquals(String.format("%s/%s", swiftSchema.getDir(), tableKey), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema).build();
        Assert.assertEquals(String.format("%s", swiftSchema.getDir()), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTableKey(tableKey)
                .setSegOrder(segOrder).asBackup().build();
        Assert.assertEquals(String.format("%s/%s/seg%d", swiftSchema.getBackupDir(), tableKey, segOrder), path);

        path = new CubePathBuilder().setSwiftSchema(swiftSchema)
                .setTableKey(tableKey)
                .setSegOrder(segOrder).asAbsolute().build();
        Assert.assertEquals(String.format("%s/%s/%s/seg%d", basePath, swiftSchema.getDir(), tableKey, segOrder), path);
    }

    @Test
    public void testBuildFromSegKey() {
        SourceKey tableKey = new SourceKey("table1");
        SwiftSchema swiftSchema = SwiftSchema.CUBE;
        int segOrder = 10;

        SegmentKey segKey = EasyMock.mock(SegmentKey.class);
        EasyMock.expect(segKey.getSwiftSchema()).andReturn(swiftSchema).anyTimes();
        EasyMock.expect(segKey.getTable()).andReturn(tableKey).anyTimes();
        EasyMock.expect(segKey.getOrder()).andReturn(segOrder).anyTimes();
        EasyMock.replay(segKey);

        String path = new CubePathBuilder(segKey).build();
        Assert.assertEquals(String.format("%s/%s/seg%d", swiftSchema.getDir(), tableKey, segOrder), path);
    }
}