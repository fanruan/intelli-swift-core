package com.fr.bi.conf.records;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.pack.XMLConfigureGenerator;
import com.fr.base.Env;
import com.fr.base.FRContext;

/**
 * This class created on 16-12-12.
 *
 * @author Kary
 * @since Advanced FineBI Analysis 1.0
 */
public class BICubeTaskRecordManager4Test extends BICubeTaskRecordManager{
    @Override
    public void persistData(long userId) {
        try {
            XMLConfigureGenerator generator = new XMLConfigureGenerator(persistUserDataName(userId) + ".xml", getValue(userId), managerTag());
            FRContext.getCurrentEnv().writeResource(generator);
            Env env=FRContext.getCurrentEnv();
            env.getSharePath();
            env.setBuildFilePath(env.getBuildFilePath()+"../testFolder/"+env.getBuildFilePath());
            FRContext.setCurrentEnv(env);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

    }

}
