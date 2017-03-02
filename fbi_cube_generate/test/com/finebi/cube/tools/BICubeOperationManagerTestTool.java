package com.finebi.cube.tools;

import com.finebi.cube.gen.arrange.BICubeOperationManager;
import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.finebi.cube.gen.oper.watcher.BICubeBuildFinishWatcher;
import com.finebi.cube.gen.oper.watcher.BIDataSourceBuildFinishWatcher;
import com.finebi.cube.gen.oper.watcher.BIPathBuildFinishWatcher;
import com.finebi.cube.tools.subset.BIFieldIndexBuilderTestTool;
import com.finebi.cube.tools.subset.BIFieldPathIndexBuilderTestTool;
import com.finebi.cube.tools.subset.bisourcedatatransportTestTool;
import com.finebi.cube.tools.subset.watcher.BICubeBuildFinishWatcherTestTool;
import com.finebi.cube.tools.subset.watcher.BIDataSourceBuildFinishTestTool;
import com.finebi.cube.tools.subset.watcher.BIPathBuildFinishWatcherTestTool;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.conf.manager.update.source.UpdateSettingSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeOperationManagerTestTool extends BICubeOperationManager {
    public BICubeOperationManagerTestTool(Cube cube, Set<CubeTableSource> originalTableSet) {
        super(cube, null, originalTableSet);
    }



    @Override
    protected BIFieldIndexGenerator getFieldIndexBuilder(Cube cube, CubeTableSource tableSource, ICubeFieldSource BICubeFieldSource, BIColumnKey targetColumnKey) {
        return new BIFieldIndexBuilderTestTool(cube, tableSource, BICubeFieldSource, targetColumnKey);
    }


    @Override
    protected BISourceDataTransport getDataTransportBuilder(Cube cube,Cube integrityCube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parent, long version, UpdateSettingSource updateSetting,Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        return new bisourcedatatransportTestTool(cube,integrityCube, tableSource, allSources, parent,tablesNeed2GenerateMap);
    }


    @Override
    protected BIFieldPathIndexBuilder getFieldPathBuilder(Cube cube, ICubeFieldSource field, BITableSourceRelationPath tablePath) {
        return new BIFieldPathIndexBuilderTestTool(cube, field, null);
    }

    @Override
    protected BIDataSourceBuildFinishWatcher getDataSourceBuildFinishWatcher() {
        return new BIDataSourceBuildFinishTestTool();
    }

    @Override
    protected BICubeBuildFinishWatcher getCubeBuildFinishWatcher() {
        return new BICubeBuildFinishWatcherTestTool();
    }

    @Override
    protected BIPathBuildFinishWatcher getPathBuildFinishWatcher() {
        return new BIPathBuildFinishWatcherTestTool();
    }

}
