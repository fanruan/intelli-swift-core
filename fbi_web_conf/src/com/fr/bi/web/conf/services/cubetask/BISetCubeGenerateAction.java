package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.CubeBuildStuff;
import com.finebi.cube.conf.CubeBuildStuffManager;
import com.finebi.cube.conf.CubeBuildStuffManagerSingleTable;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
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
//        String connectionName = WebUtils.getHTTPRequestParameter(req, "connectionName");
//        String tableName= WebUtils.getHTTPRequestParameter(req, "tableName");
//        String translations= WebUtils.getHTTPRequestParameter(req, "translations");
//        BusinessTable businessTable = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
        
       
       //todo kary 后期要优化ETL的更新方式
        if (StringUtils.isEmpty(tableId)){
            CubeBuildStuff cubeBuildStuffManager= new CubeBuildStuffManager(new BIUser(userId));
            CubeTaskBuild.CubeBuild(userId,cubeBuildStuffManager);            
        }else{
            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
//            BIPackageTableSourceConfigManager biPackageFindTableSourceConfigManager=new BIPackageTableSourceConfigManager();
//            Set<BIBusinessTable> tableSources4Genrate = biPackageFindTableSourceConfigManager.getTableSources4Genrate(userId);
//            cubeBuildStuff=new CubeBuildStuffManagerByTableSources(tableSources4Genrate,userId);
            CubeTaskBuild.CubeBuild(userId, cubeBuildStuff);
        }
    }

}
