package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.generate.BuildCubeTask;
import com.fr.bi.cal.generate.BuildCubeTaskSingleTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BICubeManagerProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISetCubeGenerateAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "set_cube_generate";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "tableId");
        BICubeManagerProvider cubeManager = BIConfigureManagerCenter.getCubeManager();
        if (StringUtils.isEmpty(tableId)){
            cubeManager.addTask(new BuildCubeTask(new BIUser(userId)), userId);
        }else{
            BITable biTable=new BITable(tableId);
            cubeManager.addTask(new BuildCubeTaskSingleTable(new BIUser(userId),biTable), userId);
        }
    }

}
