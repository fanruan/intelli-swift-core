package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.build.CubeBuildStuff;
import com.fr.bi.stable.data.BITable;
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

        if (StringUtils.isEmpty(tableId)){
            CubeTskBuild.CubeBuild(userId);            
        }else{
            CubeBuildStuff cubeBuildStuff = new BuildCubeSingleTableStuff(new BITable(tableId),userId);
            CubeTskBuild.buildCubebyBusiniessTable(userId, cubeBuildStuff,new BITable(tableId));
        }
    }

}
