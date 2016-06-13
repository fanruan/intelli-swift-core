package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.pack.imp.BIPackageTableSourceConfigManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskBuild {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public CubeTaskBuild() {
        BIPackageTableSourceConfigManager biPackageFindTableSourceConfigManager = new BIPackageTableSourceConfigManager();
        Set<BIBusinessTable> newTables = biPackageFindTableSourceConfigManager.getTable4Generate(UserControl.getInstance().getSuperManagerID());
    }

    public static void CubeBuild(long userId, CubeBuildStuff cubeBuildStuff) {

        CubeTaskBuild.CubeBuild(userId, cubeBuildStuff);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
