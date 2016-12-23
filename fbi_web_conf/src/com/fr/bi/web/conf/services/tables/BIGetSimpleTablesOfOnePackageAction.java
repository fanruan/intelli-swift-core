package com.fr.bi.web.conf.services.tables;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.BIPackageID;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * 进入业务包的时候获取简略的表信息
 * 包含表id、原始表名和转义名
 * Created by Young's on 2016/12/15.
 */
public class BIGetSimpleTablesOfOnePackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String packageId = WebUtils.getHTTPRequestParameter(req, "id");

        Set<BusinessTable> tables = BICubeConfigureCenter.getPackageManager().getPackage(userId, new BIPackageID(packageId)).getBusinessTables();
        JSONObject tablesJO = new JSONObject();
        for (BusinessTable table : tables) {
            String tableId = table.getID().getIdentityValue();
            CubeTableSource source = BusinessTableHelper.getTableDataSource(new BITableID(tableId));
            JSONObject tableJO = source.createJSON();
            tableJO.put("id", tableId);
            tablesJO.put(tableId, tableJO);
        }
        WebUtils.printAsJSON(res, tablesJO);
    }

    @Override
    public String getCMD() {
        return "get_simple_tables_of_one_package";
    }

}
