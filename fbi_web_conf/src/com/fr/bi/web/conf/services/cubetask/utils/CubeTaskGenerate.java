package com.fr.bi.web.conf.services.cubetask.utils;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerIncremental;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.data.BITableID;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
    
    public static void CubeBuild(long userId, BITableID biTableID) {
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(biTableID),userId);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }

    public static void CubeBuild(long userId) {
        BICubeConfigureCenter.getPackageManager().getPackages4CubeGenerate(userId);
        Set<BIBusinessTable> newTables = BICubeGenerateTool.getTables4CubeGenerate(userId);
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerIncremental(newTables, userId);
        CubeBuildStuff cubeBuildStuff2 = new CubeBuildStuffManager(new BIUser(userId));
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
//        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff2), userId);
    }
}
