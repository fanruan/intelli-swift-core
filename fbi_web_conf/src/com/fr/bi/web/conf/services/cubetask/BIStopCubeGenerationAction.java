package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BICubeManagerProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIStopCubeGenerationAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "stopCubeGeneration";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long userId = ServiceUtils.getCurrentUserID(req);

        BICubeManagerProvider cubeManager = BIConfigureManagerCenter.getCubeManager();
//        if (BIPackUtils.isNoPackageChange(userId) && BIPackUtils.isNoGeneratingChange(userId)) {
//            cubeManager.addTask(new AllTask(userId), userId);
//        } else {
//            cubeManager.addTask(new CheckTask(userId), userId);
//        }
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId)), userId);
    }

}