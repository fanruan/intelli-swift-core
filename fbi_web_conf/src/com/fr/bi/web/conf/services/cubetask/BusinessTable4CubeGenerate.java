package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.pack.imp.BIPackageTableSourceConfigManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerIncremental;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class BusinessTable4CubeGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public static void CubeBuild(long userId, CubeBuildStuff cubeBuildStuff) {
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }

    public static void CubeBuild(long userId) {
        BICubeConfigureCenter.getPackageManager().getPackages4CubeGenerate(userId);
//        Set<BIBusinessTable> newTables = BIPackageTableSourceConfigManager.getTables4Generate(UserControl.getInstance().getSuperManagerID());
        Set<BIBusinessTable> newTables = BIPackageTableSourceConfigManager.getAllTables(userId);
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerIncremental(newTables, userId);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
