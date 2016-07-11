package com.fr.bi.web.conf.services.cubetask.utils;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManager;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerIncremental;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public static boolean CubeBuild(long userId, BITableID biTableID) {
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(new BIBusinessTable(biTableID), userId);
        boolean taskAdd = cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
        return taskAdd;
    }

    public static boolean CubeBuild(long userId, BITableID ETLTableId, BITableID baseTableId) {
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable(new BIBusinessTable(ETLTableId), userId);
        boolean taskAdd = cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuildStuff), userId);
        return taskAdd;
    }

    public static boolean CubeBuild(long userId) {
        boolean taskAddResult = false;
        CubeBuildStuff cubeBuildStuff;
/*若有新增表或者新增关联，增量更新，否则进行全量*/
        if (isIncremental(userId)) {
            BILogger.getLogger().info("Cube incremental update start");
            cubeBuildStuff = new CubeBuildStuffManagerIncremental(userId,BICubeGenerateTool.getTables4CubeGenerate(userId),BICubeGenerateTool.getRelations4CubeGenerate(userId));
        } else {
            BILogger.getLogger().info("Cube global update start");
            cubeBuildStuff = new CubeBuildStuffManager(new BIUser(userId));
        }
        if (preConditionsCheck(userId, cubeBuildStuff)) {
            CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuildStuff);
            taskAddResult = cubeManager.addTask(task, userId);
        }
        return taskAddResult;
    }

    private  static boolean isIncremental(long userId) {
        Set<BIBusinessTable> newTables = BICubeGenerateTool.getTables4CubeGenerate(userId);
        Set<BITableRelation> newRelationSet = BICubeGenerateTool.getRelations4CubeGenerate(userId);
        boolean isIncremental = newTables.size() > 0 || newRelationSet.size() > 0;
        return isIncremental;
    }

    private static boolean preConditionsCheck(long userId, CubeBuildStuff cubeBuildStuff) {
        boolean conditionsMeet = cubeBuildStuff.preConditionsCheck();
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
            BILogger.getLogger().error(errorMessage);
            BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
            BIConfigureManagerCenter.getLogManager().logEnd(userId);
        }
        return conditionsMeet;
    }
}
