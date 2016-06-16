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
import com.fr.fs.control.UserControl;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public static void CubeBuild(long userId, CubeBuildStuff cubeBuildStuff) {
        if (null != cubeBuildStuff && BICubeConfigureCenter.getPackageManager().getAllPackages(userId).size() != 0) {
            cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
        }
    }

    public static void CubeBuild(long userId) {
        BICubeConfigureCenter.getPackageManager().getPackages4CubeGenerate(userId);
        BIPackageTableSourceConfigManager biPackageFindTableSourceConfigManager = new BIPackageTableSourceConfigManager();
        Set<BIBusinessTable> newTables = biPackageFindTableSourceConfigManager.getTables4Generate(UserControl.getInstance().getSuperManagerID());
        if(newTables.size()==0){
            return;
        }
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerIncremental(newTables, -999);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }
}
