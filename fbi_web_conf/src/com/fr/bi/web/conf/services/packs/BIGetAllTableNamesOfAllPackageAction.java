package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.base.pack.data.BIBusinessPackage;
import com.fr.bi.conf.base.pack.data.BIBusinessTable;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.ITableSource;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author windy
 * 获取所有业务包中的表的原始表名，关联视图时使用
 */
public class BIGetAllTableNamesOfAllPackageAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        Set<BIBusinessPackage> packs = BIConfigureManagerCenter.getPackageManager().getAllPackages(userId);
        JSONObject tableJO = new JSONObject();
        for(BIBusinessPackage pack : packs) {
            Set<BIBusinessTable> tables = pack.getBusinessTables();
            for(BIBusinessTable table : tables) {
                ITableSource src = table.getSource();
                if(src.getType() == BIBaseConstant.TABLETYPE.DB){
                    tableJO.put(table.getID().getIdentity(), table.getSource().getDbTable().getTableName());
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
