package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.impl.conf.CubeBuildStuffManagerSingleTable;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.bi.web.conf.services.cubetask.utils.CubeTaskGenerate;
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
//        todo kary 优化ETL的更新方式,可能要单独实现ETL更新方法
        if (StringUtils.isEmpty(tableId)){
//            CubeBuildStuff cubeBuildStuff= new CubeBuildStuffManager(new BIUser(userId));
//            CubeTaskGenerate.CubeBuild(userId, cubeBuildStuff);
            CubeTaskGenerate.CubeBuild(userId);
        }else{
            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
            CubeTaskGenerate.CubeBuild(userId, cubeBuildStuff);
        }
    }

}
