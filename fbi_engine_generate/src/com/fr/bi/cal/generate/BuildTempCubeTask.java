package com.fr.bi.cal.generate;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.CubeBuild;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.db.PersistentTable;
import com.fr.general.DateUtils;

import java.util.concurrent.Future;

/**
 * Created by roy on 16/10/10.
 */
public class BuildTempCubeTask extends BuildCubeTask {
    public BuildTempCubeTask(BIUser biUser, CubeBuild cubeBuild) {
        super(biUser, cubeBuild);
    }

    @Override
    public void end() {
        Future<String> result = finishObserver.getOperationResult();
        try {
            String message = result.get();
            BILoggerFactory.getLogger().info(message);
            boolean cubeBuildSucceed = finishObserver.success();
            if (!cubeBuildSucceed) {
                checkTaskFinish();
            }

            if (cubeBuildSucceed) {
                cube.addVersion(System.currentTimeMillis());
                long start = System.currentTimeMillis();
                BICubeConfigureCenter.getTableRelationManager().finishGenerateCubes(biUser.getUserId());
                BICubeConfigureCenter.getTableRelationManager().persistData(biUser.getUserId());
                BILoggerFactory.getLogger().info("Replace successful! Cost :" + DateUtils.timeCostFrom(start));

            } else {
                message = "Cube build failed ,the Cube files will not be replaced ";
                BIConfigureManagerCenter.getLogManager().errorTable(new PersistentTable("", "", ""), message, biUser.getUserId());
                BILoggerFactory.getLogger().error(message);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        } finally {

        }
    }
}
