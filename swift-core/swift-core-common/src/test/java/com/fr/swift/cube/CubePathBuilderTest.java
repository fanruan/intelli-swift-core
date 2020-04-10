package com.fr.swift.cube;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.service.SwiftCubePathService;
import com.fr.swift.db.SwiftDatabase;
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

        SwiftCubePathService pathService = EasyMock.mock(SwiftCubePathService.class);
        basePath = "/base_path";
        EasyMock.expect(pathService.getSwiftPath()).andReturn(basePath).anyTimes();

        EasyMock.expect(swiftContext.getBean(SwiftCubePathService.class)).andReturn(pathService).anyTimes();
        EasyMock.replay(pathService, swiftContext);
    }

    @Test(expected = RuntimeException.class)
    public void testWrongBuild() {
        new CubePathBuilder().build();
    }

    @Test(expected = RuntimeException.class)
    public void testWrongBuild1() {
        new CubePathBuilder().setSwiftSchema(SwiftDatabase.CUBE).setTempDir(1).asBackup().build();
    }

    @Test
    public void testBuild() {
        SourceKey tableKey = new SourceKey("table1");
        SwiftDatabase swiftSchema = SwiftDatabase.CUBE;
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
        SwiftDatabase swiftSchema = SwiftDatabase.CUBE;
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