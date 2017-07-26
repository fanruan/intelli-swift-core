package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cluster.utils.BIUserAuthUtils;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.web.service.utils.BIAnalysisTableHelper;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kary on 17-1-6.
 */
public class BIAnalysisETLCheckAllTableStatusAction extends AbstractAnalysisETLAction {
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        final long userId = BIUserAuthUtils.getCurrentUserID(req);
        Map processesMap = new HashMap();
        for (BusinessTable table : BIAnalysisETLManagerCenter.getBusiPackManager().getAllTables(userId)) {
            String tableId = table.getID().getIdentityValue();
            double percent = BIAnalysisTableHelper.getTableGeneratingProcessById(tableId, userId);
            processesMap.put(tableId, percent);
        }
        WebUtils.printAsJSON(res, new JSONObject().put(Constants.ALL_TABLE_GENERATED_PERCENT, processesMap));
    }


    @Override
    public String getCMD() {
        return "check_all_table_status";
    }

}
