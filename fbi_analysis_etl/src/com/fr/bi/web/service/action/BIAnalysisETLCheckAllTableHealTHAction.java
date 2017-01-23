package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLCheckAllTableHealTHAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = ServiceUtils.getCurrentUserID(req);
        Map processesMap = new HashMap();
        Map healthMap = new HashMap();
        JSONObject result = JSONObject.create();
        Set<BusinessTable> allTables =  BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(userId);
        for (BusinessTable table : allTables) {
            if (null == BIModuleUtils.getBusinessTableById(table.getID())) {
                continue;
            }
            String tableId = table.getID().getIdentityValue();
            boolean health = BIAnalysisTableHelper.getTableHealthById(tableId, userId);
            healthMap.put(tableId, health);
            double percent = BIAnalysisTableHelper.getTableGeneratingProcessById(tableId, userId);
            processesMap.put(tableId, percent);
        }
        WebUtils.printAsJSON(res, result.put(Constants.ALL_TABLE_GENERATED_HEALTH, healthMap).put(Constants.ALL_TABLE_GENERATED_PERCENT, processesMap));
    }


    @Override
    public String getCMD() {
        return "check_all_table_health";
    }

}
