package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.build.CubeBuildStuff;
import com.finebi.cube.conf.build.CubeBuildStuffManager;
import com.finebi.cube.conf.build.CubeBuildStuffManagerSingleTable;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
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
        String connectionName = WebUtils.getHTTPRequestParameter(req, "connectionName");
        String tableName= WebUtils.getHTTPRequestParameter(req, "tableName");
        String translations= WebUtils.getHTTPRequestParameter(req, "translations");


        BusinessTable businessTable = BusinessTableHelper.getBusinessTable(new BITableID(tableId));
        businessTable.getTableSource();
        
        CubeBuildStuff cubeBuildStuffTest = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
        BIBusinessTable biBusinessTable = new BIBusinessTable(new BITableID(tableId));
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
        for (IBusinessPackageGetterService pack : packs) {
            Iterator<BIBusinessTable> tIt = pack.getBusinessTables().iterator();
            while (tIt.hasNext()) {
                BIBusinessTable table = tIt.next();
                if (ComparatorUtils.equals(table.getID(),biBusinessTable.getID())) {
                    biBusinessTable=table;
                }
            }
        }
       
        
        if (StringUtils.isEmpty(tableId)){
            CubeBuildStuff cubeBuildStuffManager= new CubeBuildStuffManager(new BIUser(userId));
            CubeTskBuild.CubeBuild(userId,cubeBuildStuffManager);            
        }else{
            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
            CubeTskBuild.CubeBuild(userId, cubeBuildStuff);
        }
    }

}
