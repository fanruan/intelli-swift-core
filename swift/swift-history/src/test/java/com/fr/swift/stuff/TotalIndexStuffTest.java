package com.fr.swift.stuff;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.task.TaskKey;
import com.fr.swift.task.cube.CubeOperation;
import com.fr.swift.task.impl.SwiftTaskKey;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import java.util.Map;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TotalIndexStuffTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Ignore
    @Test
    public void testTotalIndexStuff() {
        Map<TaskKey, DataSource> tables = EasyMock.mock(Map.class);
        Map<TaskKey, RelationSource> relations = EasyMock.mock(Map.class);
        Map<TaskKey, SourcePath> relationPaths = EasyMock.mock(Map.class);
        SourcePath sourcePath = EasyMock.mock(SourcePath.class);

        TaskKey taskKey1 = new SwiftTaskKey(0, "0", CubeOperation.BUILD_TABLE);
        // Generate by Mock Plugin
        DataSource dataSource = PowerMock.createMock(DataSource.class);
        PowerMock.replayAll();
        EasyMock.expect(tables.get(taskKey1)).andReturn(dataSource);

        TaskKey taskKey2 = new SwiftTaskKey(1, "1", CubeOperation.INDEX_RELATION);
        RelationSource relation = new RelationSourceImpl();
        EasyMock.expect(relations.get(taskKey2)).andReturn(relation);

        TaskKey taskKey3 = new SwiftTaskKey(2, "2", CubeOperation.INDEX_PATH);
        EasyMock.expect(relationPaths.get(taskKey3)).andReturn(sourcePath);
        EasyMock.replay(tables, relations, relationPaths, sourcePath);

        HistoryIndexingStuff stuff = new HistoryIndexingStuff(tables, relations, relationPaths);
        assertEquals(stuff.getTables().get(taskKey1), dataSource);
        assertEquals(stuff.getRelations().get(taskKey2), relation);
        assertEquals(stuff.getRelationPaths().get(taskKey3), sourcePath);
    }
}
