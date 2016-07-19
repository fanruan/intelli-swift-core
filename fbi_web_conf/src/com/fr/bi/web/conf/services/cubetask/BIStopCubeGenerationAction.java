package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeManagerProvider;
import com.finebi.cube.conf.CubeBuild;
import com.finebi.cube.impl.conf.CubeBuildStaff;
import com.finebi.cube.conf.CubeGenerationManager;
import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
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
        BICubeManagerProvider cubeManager = CubeGenerationManager.getCubeManager();
        CubeBuild cubeBuild =new CubeBuildStaff(new BIUser(userId));
        cubeManager.addTask(new BuildCubeTask(new BIUser(userId), cubeBuild), userId);
    }

}
