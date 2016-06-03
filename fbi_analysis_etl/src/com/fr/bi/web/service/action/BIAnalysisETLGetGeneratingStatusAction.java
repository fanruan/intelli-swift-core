package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 小灰灰 on 2016/6/2.
 */
public class BIAnalysisETLGetGeneratingStatusAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
        String sourceID = ((AnalysisCubeTableSource)table.getTableSource()).createUserTableSource(userId).getSourceID();
        boolean isGenerated = !StringUtils.isEmpty(BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().getCubePath(sourceID));
        boolean isGenerating = BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().isCubeGenerating(sourceID);
        double percent = isGenerating ? 0.5 : isGenerated ? 1 : 0.1;
        JSONObject jo = new JSONObject();
        jo.put(Constants.GENERATED_PERCENT, percent);
        WebUtils.printAsJSON(res, jo);
    }


    @Override
    public String getCMD() {
        return "get_cube_status";
    }
}

