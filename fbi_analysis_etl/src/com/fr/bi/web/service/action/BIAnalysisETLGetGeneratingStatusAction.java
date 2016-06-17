package com.fr.bi.web.service.action;

import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.etl.analysis.data.AnalysisCubeTableSource;
import com.fr.bi.etl.analysis.manager.BIAnalysisETLManagerCenter;
import com.fr.bi.stable.exception.BITableAbsentException;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 小灰灰 on 2016/6/2.
 */
public class BIAnalysisETLGetGeneratingStatusAction extends AbstractAnalysisETLAction{
    @Override
    public void actionCMD(HttpServletRequest req, HttpServletResponse res, String sessionID) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "id");
        double percent;
        try {
            BusinessTable table = BIAnalysisETLManagerCenter.getBusiPackManager().getTable(tableId, userId);
            Set<AnalysisCubeTableSource> sources = new HashSet<AnalysisCubeTableSource>();
            ((AnalysisCubeTableSource)table.getTableSource()).getSourceUsedAnalysisETLSource(sources);
            int generated = 0;
            for (AnalysisCubeTableSource s : sources){
                if (BIAnalysisETLManagerCenter.getUserETLCubeManagerProvider().checkVersion(s, new BIUser(userId))){
                    generated ++;
                }
            }
            percent = generated == sources.size()? 1 : (0.1 + 0.9 * generated / sources.size());
        } catch (BITableAbsentException e){
            percent = 0.1;
        }
        JSONObject jo = new JSONObject();
        jo.put(Constants.GENERATED_PERCENT, percent);
        WebUtils.printAsJSON(res, jo);
    }


    @Override
    public String getCMD() {
        return "get_cube_status";
    }
}

