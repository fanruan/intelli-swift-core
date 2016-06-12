package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeBuildStuffManagerIncremental;
import com.finebi.cube.conf.pack.imp.BIPackageTableSourceConfigManager;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

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
//        String connectionName = WebUtils.getHTTPRequestParameter(req, "connectionName");
//        String tableName= WebUtils.getHTTPRequestParameter(req, "tableName");
//        String translations= WebUtils.getHTTPRequestParameter(req, "translations");
//        BusinessTable businessTable = BusinessTableHelper.getBusinessTable(new BITableID(tableId));

        BIPackageTableSourceConfigManager biPackageFindTableSourceConfigManager = new BIPackageTableSourceConfigManager();
        Set<BIBusinessTable> tableSources4Genrate = biPackageFindTableSourceConfigManager.getTableSources4Genrate(userId);
        CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerIncremental(tableSources4Genrate, userId);
        CubeTaskBuild.CubeBuild(userId, cubeBuildStuff);
        //todo kary 优化ETL的更新方式,可能要单独实现ETL更新方法
//        if (StringUtils.isEmpty(tableId)){
//            CubeBuildStuff cubeBuildStuffManager= new CubeBuildStuffManager(new BIUser(userId));
//            CubeTaskBuild.CubeBuild(userId,cubeBuildStuffManager);
//        }else{
//            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
//            CubeTaskBuild.CubeBuild(userId, cubeBuildStuff);
//        }
    }

}
