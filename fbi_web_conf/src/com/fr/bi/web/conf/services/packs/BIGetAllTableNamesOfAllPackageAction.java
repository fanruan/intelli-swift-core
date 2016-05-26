package com.fr.bi.web.conf.services.packs;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author windy
 *         获取所有业务包中的表的原始表名，关联视图时使用
 */
public class BIGetAllTableNamesOfAllPackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
        JSONObject tableJO = new JSONObject();
        for (IBusinessPackageGetterService pack : packs) {
            Set<BIBusinessTable> tables = pack.getBusinessTables();
            for (BIBusinessTable table : tables) {
                CubeTableSource src = table.getTableSource();
                if (src.getType() == BIBaseConstant.TABLETYPE.DB) {
                    tableJO.put(table.getID().getIdentity(), table.getTableSource().getPersistentTable().getTableName());
                }
            }
        }
        WebUtils.printAsJSON(res, tableJO);
    }

    @Override
    public String getCMD() {
        return "get_table_names_of_all_packages";
    }
}
