package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.pack.imp.BIPackageTableSourceConfigManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskBuild {

    private static BICubeManagerProvider cubeManager= CubeGenerationManager.getCubeManager();

    public static void CubeBuild(long userId,CubeBuildStuff cubeBuildStuff){
        BIPackageTableSourceConfigManager biPackageFindTableSourceConfigManager=new BIPackageTableSourceConfigManager();
        Set<CubeTableSource> cubeTableSources = biPackageFindTableSourceConfigManager.getTableSources4Genrate(userId);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId),cubeBuildStuff),userId);
    }
}
