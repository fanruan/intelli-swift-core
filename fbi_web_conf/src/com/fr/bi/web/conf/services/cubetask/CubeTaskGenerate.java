package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.conf.CubeGenerationManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildByPart;
import com.finebi.cube.impl.conf.CubeBuildStaff;
import com.finebi.cube.impl.conf.CubeBuildSingleTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.bi.stable.engine.CubeTask;
import com.fr.bi.stable.utils.code.BILogger;
import com.fr.bi.web.conf.services.utils.BICubeGenerateUtils;

import java.util.Set;

/**
 * Created by wuk on 16/5/30.
 */
public class CubeTaskGenerate {

    private static BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();

    public static boolean CubeBuildSingleTable(long userId, BITableID biTableID) {
        CubeBuild cubeBuild = new CubeBuildSingleTable(new BIBusinessTable(biTableID), userId);
        boolean taskAdd = cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
        return taskAdd;
    }

    public static boolean CubeBuildETL(long userId, BITableID ETLTableId, BITableID baseTableId) {
        CubeBuild cubeBuild = new CubeBuildSingleTable(new BIBusinessTable(ETLTableId), userId);
        boolean taskAdd = cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
        return taskAdd;
    }

    public static boolean CubeBuildStaff(long userId) {
        boolean taskAddResult = false;
        CubeBuild cubeBuild;
/*若有新增表或者新增关联，增量更新，否则进行全量*/
        if (isPart(userId)) {
            BILogger.getLogger().info("Cube part update start");
            cubeBuild = new CubeBuildByPart(userId, BICubeGenerateUtils.getTables4CubeGenerate(userId), BICubeGenerateUtils.getRelations4CubeGenerate(userId));
        } else {
            BILogger.getLogger().info("Cube all update start");
            cubeBuild = new CubeBuildStaff(new BIUser(userId));
        }
        if (preConditionsCheck(userId, cubeBuild)) {
            CubeTask task = new BuildCubeTask(new BIUser(userId), cubeBuild);
            taskAddResult = cubeManager.addTask(task, userId);
        }
        return taskAddResult;
    }

    private  static boolean isPart(long userId) {
        Set<BIBusinessTable> newTables = BICubeGenerateUtils.getTables4CubeGenerate(userId);
        Set<BITableRelation> newRelationSet = BICubeGenerateUtils.getRelations4CubeGenerate(userId);
        boolean isPart = newTables.size() > 0 || newRelationSet.size() > 0;
        return isPart;
    }

    private static boolean preConditionsCheck(long userId, CubeBuild cubeBuild) {
        boolean conditionsMeet = cubeBuild.preConditionsCheck();
        if (!conditionsMeet) {
            String errorMessage = "preConditions check failed! Please check the available HD space and data connections";
            BILogger.getLogger().error(errorMessage);
            BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), errorMessage, userId);
            BIConfigureManagerCenter.getLogManager().logEnd(userId);
        }
        return conditionsMeet;
    }
}
