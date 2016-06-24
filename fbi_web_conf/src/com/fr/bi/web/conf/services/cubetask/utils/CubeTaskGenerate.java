package com.fr.bi.web.conf.services.cubetask.utils;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.utils.code.BILogger;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public static void CubeBuild(long userId, BITableID biTableID) {
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(new BIBusinessTable(biTableID), userId);
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
    }

    public static void CubeBuild(long userId) {
//        Set<BIBusinessTable> newTables = BICubeGenerateTool.getTables4CubeGenerate(userId);
///*若有新增表，增量更新，否则进行全量*/
//        if (newTables.size() != 0) {
//            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerIncremental(newTables, userId);
//            CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuildStuff);
//            if (!cubeBuildStuff.preConditionsCheck()) {
//                BILogger.getLogger().error("cube生成的前置条件无法满足，请确认硬盘空间足够且数据连接正常！");
//                BIConfigureManagerCenter.getLogManager().logEnd(userId);
//                return;
//            }
//            cubeManager.addTask(task, userId);
//        } else {
//            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(new BIUser(userId));
//            if (!cubeBuildStuff.preConditionsCheck()) {
//                BILogger.getLogger().error("cube生成的前置条件无法满足，请确认硬盘空间足够且数据连接正常！");
//                BIConfigureManagerCenter.getLogManager().logEnd(userId);
//                return;
//            }
//            cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
//        }
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManager(new BIUser(userId));
        if (!cubeBuildStuff.preConditionsCheck()) {
            BILogger.getLogger().error("cube生成的前置条件无法满足，请确认硬盘空间足够且数据连接正常！");
//            BIConfigureManagerCenter.getLogManager().logEnd(userId);
//            return;
        }
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);

    }
}
